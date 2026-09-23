package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.bip39.Bip39RecoveryEngine
import com.example.data.bip39.Bip39WordList
import com.example.data.bip39.MultiRecoveryCandidate
import com.example.data.bip39.ValidationResult
import com.example.data.local.AppDatabase
import com.example.data.local.Bip39WordDao
import com.example.data.local.ScanHistoryDao
import com.example.data.local.ScanHistoryEntity
import com.example.data.local.TrackedAddressDao
import com.example.data.local.TrackedAddressEntity
import com.example.data.network.AddressFormat
import com.example.data.network.AddressFormatDetector
import com.example.data.network.BlockchairApiService
import com.example.data.network.BlockchainApiService
import com.example.data.network.Chain
import com.example.data.network.WalletScanResult
import com.example.security.SecurityHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PartialValidationResult(
    val isValid: Boolean,
    val remainingWordsCount: Int,
    val totalWords: Int,
    val message: String,
    val invalidWords: List<Pair<Int, String>> = emptyList()
)

class WalletRecoveryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val bip39WordDao: Bip39WordDao = db.bip39WordDao()
    val trackedAddressDao: TrackedAddressDao = db.trackedAddressDao()
    val scanHistoryDao: ScanHistoryDao = db.scanHistoryDao()

    private val blockchairService = BlockchairApiService.create()
    private val blockchainFallbackService = BlockchainApiService()
    private val prefs = SecurityHelper.getSecurePrefs(application)

    // First-run Disclaimer state
    val isDisclaimerAccepted = mutableStateOf(
        prefs.getBoolean(SecurityHelper.KEY_DISCLAIMER_ACCEPTED, false)
    )

    fun acceptDisclaimer() {
        prefs.edit().putBoolean(SecurityHelper.KEY_DISCLAIMER_ACCEPTED, true).apply()
        isDisclaimerAccepted.value = true
    }

    // Navigation tab
    var currentTab = mutableIntStateOf(0)
        private set

    fun setTab(index: Int) {
        currentTab.intValue = index
    }

    // Watchlist Flow
    val trackedAddresses: StateFlow<List<TrackedAddressEntity>> = trackedAddressDao.getAllTrackedAddresses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Scan History Flow
    val scanHistory: StateFlow<List<ScanHistoryEntity>> = scanHistoryDao.getAllScans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Prepopulate BIP-39 2048 words into Room if not present
        viewModelScope.launch(Dispatchers.IO) {
            db.prepopulateBip39Words()
        }
    }

    // ==========================================
    // 1. Seed Validator (12 or 24 words)
    // ==========================================
    var validatorWordCountMode = mutableIntStateOf(12) // 12 or 24 words
    val validatorWords = mutableStateListOf<String>().apply {
        repeat(24) { add("") }
    }
    var validatorResult = mutableStateOf<ValidationResult?>(null)

    fun setValidatorWordCount(count: Int) {
        validatorWordCountMode.intValue = count
        validatorResult.value = null
    }

    fun setValidatorWord(index: Int, word: String) {
        if (index in 0 until 24) {
            validatorWords[index] = word
            validatorResult.value = null
        }
    }

    fun clearValidatorWords() {
        for (i in 0 until 24) {
            validatorWords[i] = ""
        }
        validatorResult.value = null
    }

    /**
     * اعتبارسنجی کامل عبارت ۱۲ یا ۲۴ کلمه‌ای
     */
    fun validateFullMnemonic(words: List<String>): Boolean {
        val nonBlank = words.filter { it.isNotBlank() }
        if (nonBlank.size != 12 && nonBlank.size != 24) return false

        // Check dictionary
        for (w in nonBlank) {
            if (!Bip39WordList.isValidWord(w.trim().lowercase())) {
                return false
            }
        }

        return if (nonBlank.size == 12) {
            Bip39RecoveryEngine.validatePhrase(nonBlank).isValid
        } else {
            // 24-word checksum check (8-bit SHA-256)
            validate24WordChecksum(nonBlank)
        }
    }

    private fun validate24WordChecksum(words: List<String>): Boolean {
        try {
            val indices = words.map { Bip39WordList.wordToIndex[it.trim().lowercase()] ?: return false }
            val bits = BooleanArray(264)
            for (i in 0 until 24) {
                val idx = indices[i]
                for (b in 0 until 11) {
                    bits[i * 11 + b] = ((idx ushr (10 - b)) and 1) == 1
                }
            }
            val entropy = ByteArray(32)
            for (byteIdx in 0 until 32) {
                var v = 0
                for (bit in 0 until 8) {
                    if (bits[byteIdx * 8 + bit]) {
                        v = v or (1 shl (7 - bit))
                    }
                }
                entropy[byteIdx] = v.toByte()
            }
            val md = java.security.MessageDigest.getInstance("SHA-256")
            val hash = md.digest(entropy)
            val expected = hash[0].toInt() and 0xFF

            var actual = 0
            for (bit in 0 until 8) {
                if (bits[256 + bit]) {
                    actual = actual or (1 shl (7 - bit))
                }
            }
            return expected == actual
        } catch (_: Exception) {
            return false
        }
    }

    fun runValidatorCheck() {
        val targetSize = validatorWordCountMode.intValue
        val wordsToCheck = validatorWords.take(targetSize)

        // Check blank words
        val blanks = wordsToCheck.indices.filter { wordsToCheck[it].isBlank() }
        if (blanks.isNotEmpty()) {
            validatorResult.value = ValidationResult(
                isValid = false,
                errorMessage = "تعداد ${blanks.size} کلمه هنوز وارد نشده است. تمام $targetSize کلمه را کامل کنید."
            )
            return
        }

        // Check dictionary validity
        val invalidWords = mutableListOf<Pair<Int, String>>()
        for (i in wordsToCheck.indices) {
            val w = wordsToCheck[i].trim().lowercase()
            if (!Bip39WordList.isValidWord(w)) {
                invalidWords.add(i to wordsToCheck[i])
            }
        }

        if (invalidWords.isNotEmpty()) {
            validatorResult.value = ValidationResult(
                isValid = false,
                errorMessage = "تعداد ${invalidWords.size} کلمه در فرهنگ لغت استاندارد ۲۰۴۸ کلمه‌ای یافت نشدند!",
                invalidWordIndices = invalidWords
            )
            return
        }

        // Checksum check
        val ok = validateFullMnemonic(wordsToCheck)
        if (ok) {
            validatorResult.value = ValidationResult(
                isValid = true,
                errorMessage = "عبارت $targetSize کلمه‌ای کاملاً معتبر است و چک‌سام SHA-256 تأیید گردید."
            )
        } else {
            validatorResult.value = ValidationResult(
                isValid = false,
                errorMessage = "چک‌سام عبارت معتبر نیست! ممکن است ترتیب کلمات جابجا شده باشد یا یکی از کلمات غلط املایی داشته باشد."
            )
        }
    }

    // ==========================================
    // 2. Missing Words Recovery Logic
    // ==========================================
    val missingWordsInput = mutableStateListOf<String>().apply {
        repeat(12) { add("") }
    }
    val missingIndices = mutableStateListOf<Int>(11)
    val prefixHints = mutableStateListOf<String>().apply {
        repeat(12) { add("") }
    }

    var partialValidationState = mutableStateOf<PartialValidationResult?>(null)
    var isSearchingMissing = mutableStateOf(false)
    var searchTestedCount = mutableStateOf(0)
    var searchFoundCandidates = mutableStateOf<List<MultiRecoveryCandidate>>(emptyList())
    private var searchJob: Job? = null

    fun toggleMissingWordIndex(index: Int) {
        if (missingIndices.contains(index)) {
            missingIndices.remove(index)
        } else {
            if (missingIndices.size < 3) {
                missingIndices.add(index)
                missingIndices.sort()
            }
        }
        searchFoundCandidates.value = emptyList()
        partialValidationState.value = null
    }

    fun setMissingWordInput(index: Int, word: String) {
        if (index in 0 until 12) {
            missingWordsInput[index] = word
            validatePartialMnemonic(missingWordsInput.toList())
        }
    }

    fun setMissingPrefixHint(index: Int, hint: String) {
        if (index in 0 until 12) {
            prefixHints[index] = hint
        }
    }

    /**
     * Requirements:
     * 1. validatePartialMnemonic(words: List<String>)
     *    - چک کردن هر کلمه با دیکشنری 2048
     *    - تشخیص کلمات نامعتبر
     *    - محاسبه تعداد کلمات باقیمانده
     *    - برای 9/10/11 کلمه: نمایش پیام راهنما
     */
    fun validatePartialMnemonic(words: List<String>): PartialValidationResult {
        val filled = words.indices.filter { words[it].isNotBlank() }
        val remaining = 12 - filled.size

        val invalidWords = mutableListOf<Pair<Int, String>>()
        for (i in filled) {
            val w = words[i].trim().lowercase()
            if (!Bip39WordList.isValidWord(w)) {
                invalidWords.add(i to words[i])
            }
        }

        val message = when {
            invalidWords.isNotEmpty() -> "تعداد ${invalidWords.size} کلمه در فهرست استاندارد وجود ندارد."
            remaining == 0 -> "تمام ۱۲ کلمه وارد شده است. آماده اعتبارسنجی نهایی."
            remaining in 1..3 -> "تعداد $remaining کلمه مفقود است. با فشردن دکمه جستجو، الگوریتم تمام احتمالات ریاضی معتبر را پیدا خواهد کرد."
            else -> "حداقل ۹ کلمه معتبر باید وارد شود تا جستجوی کلمات مفقود فعال شود (کلمات باقیمانده: $remaining)."
        }

        val res = PartialValidationResult(
            isValid = invalidWords.isEmpty() && remaining in 0..3,
            remainingWordsCount = remaining,
            totalWords = 12,
            message = message,
            invalidWords = invalidWords
        )
        partialValidationState.value = res
        return res
    }

    /**
     * Requirements:
     * 2. findMissingWords(knownWords: List<String>): Flow<List<String>>
     *    - brute-force هوشمند روی کلمات نامعلوم
     *    - بررسی checksum SHA-256
     *    - کاملاً آفلاین، در RAM
     *    - Progress قابل لغو
     */
    fun findMissingWords(knownWords: List<String>): Flow<List<MultiRecoveryCandidate>> = flow {
        val missing = missingIndices.toList()
        val hintsMap = missing.associateWith { prefixHints[it] }

        val candidates = Bip39RecoveryEngine.findMultiMissingWordCandidates(
            words = knownWords,
            missingIndices = missing,
            hints = hintsMap,
            maxResults = if (missing.size == 3) 50 else 100,
            onProgress = { tested, _ ->
                searchTestedCount.value = tested
            }
        )
        emit(candidates)
    }.flowOn(Dispatchers.Default)

    fun startMissingWordSearch() {
        if (missingIndices.isEmpty()) return
        searchJob?.cancel()
        isSearchingMissing.value = true
        searchTestedCount.value = 0
        searchFoundCandidates.value = emptyList()

        searchJob = viewModelScope.launch {
            findMissingWords(missingWordsInput.toList()).collect { candidates ->
                searchFoundCandidates.value = candidates
                isSearchingMissing.value = false
            }
        }
    }

    fun cancelMissingWordSearch() {
        searchJob?.cancel()
        searchJob = null
        isSearchingMissing.value = false
    }

    fun applyMissingCandidate(candidate: MultiRecoveryCandidate) {
        candidate.recoveredWords.forEach { (idx, word) ->
            missingWordsInput[idx] = word
        }
        missingIndices.clear()
        searchFoundCandidates.value = emptyList()
        validatePartialMnemonic(missingWordsInput.toList())
    }

    fun loadMissingPreset(preset: Int) {
        val sample = when (preset) {
            1 -> listOf("abandon", "abandon", "abandon", "abandon", "abandon", "abandon", "abandon", "abandon", "abandon", "abandon", "abandon", "about")
            2 -> listOf("legal", "winner", "thank", "year", "wave", "sausage", "worth", "useful", "legal", "winner", "thank", "yellow")
            else -> listOf("letter", "advice", "cage", "absurd", "amount", "doctor", "acoustic", "avoid", "letter", "advice", "cage", "above")
        }
        for (i in 0 until 12) {
            missingWordsInput[i] = sample[i]
            prefixHints[i] = ""
        }
        missingIndices.clear()
        when (preset) {
            1 -> missingIndices.addAll(listOf(11))
            2 -> missingIndices.addAll(listOf(10, 11))
            else -> missingIndices.addAll(listOf(9, 10, 11))
        }
        searchFoundCandidates.value = emptyList()
        validatePartialMnemonic(missingWordsInput.toList())
    }

    fun clearMissingWords() {
        for (i in 0 until 12) {
            missingWordsInput[i] = ""
            prefixHints[i] = ""
        }
        missingIndices.clear()
        searchFoundCandidates.value = emptyList()
        partialValidationState.value = null
    }

    // ==========================================
    // 3. Balance Scanner & 24h Room Cache
    // ==========================================
    var scannerAddress = mutableStateOf("")
    var selectedChain = mutableStateOf(Chain.BITCOIN)
    var detectedFormat = mutableStateOf(AddressFormat.UNKNOWN)
    var isScanning = mutableStateOf(false)
    var scanResult = mutableStateOf<WalletScanResult?>(null)
    var isFromCache = mutableStateOf(false)

    fun onScannerAddressChange(addr: String) {
        scannerAddress.value = addr
        val format = AddressFormatDetector.detectAddressFormat(addr)
        detectedFormat.value = format
        if (format.chain != Chain.UNKNOWN) {
            selectedChain.value = format.chain
        }
    }

    /**
     * Requirements:
     * 4. checkAddressBalance(address: String, chain: Chain)
     *    - Blockchair API
     *    - Cache در Room (24 ساعت)
     *    - Fallback به Blockchain.com
     */
    fun checkAddressBalance(address: String, chain: Chain = selectedChain.value) {
        val trimmed = address.trim()
        if (trimmed.isBlank()) return

        scannerAddress.value = trimmed
        viewModelScope.launch {
            isScanning.value = true
            isFromCache.value = false

            // 1. Check 24-hour cache in Room
            val twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
            val cached = scanHistoryDao.getCachedScan(trimmed, chain.name)

            if (cached != null && cached.cachedAt > twentyFourHoursAgo) {
                // Use Cache
                scanResult.value = WalletScanResult(
                    address = cached.address,
                    detectedChain = cached.blockchain,
                    balance = cached.balance,
                    balanceSymbol = cached.balanceSymbol,
                    txCount = cached.txCount,
                    explorerUrl = "https://blockchair.com/${chain.name.lowercase()}/address/$trimmed",
                    details = "دریافت شده از حافظه کش محلی (۲۴ ساعته) | ${cached.details}"
                )
                isFromCache.value = true
                isScanning.value = false
                return@launch
            }

            // 2. Query Blockchair API directly via Retrofit
            var result: WalletScanResult? = null

            if (chain == Chain.BITCOIN) {
                try {
                    val resp = withContext(Dispatchers.IO) { blockchairService.getBitcoinAddress(trimmed) }
                    if (resp.isSuccessful && resp.body()?.data != null) {
                        val btcData = resp.body()?.data?.get(trimmed)?.address
                        if (btcData != null) {
                            val satoshis = btcData.balance ?: 0.0
                            val btcAmount = satoshis / 100_000_000.0
                            result = WalletScanResult(
                                address = trimmed,
                                detectedChain = "Bitcoin",
                                balance = btcAmount.toString(),
                                balanceSymbol = "BTC",
                                txCount = btcData.transactionCount ?: 0,
                                explorerUrl = "https://blockchair.com/bitcoin/address/$trimmed",
                                details = "استعلام زنده از Blockchair API: $satoshis Satoshis"
                            )
                        }
                    }
                } catch (_: Exception) {}
            } else if (chain == Chain.ETHEREUM) {
                try {
                    val resp = withContext(Dispatchers.IO) { blockchairService.getEthereumAddress(trimmed) }
                    if (resp.isSuccessful && resp.body()?.data != null) {
                        val ethData = resp.body()?.data?.get(trimmed.lowercase())?.address
                        if (ethData != null) {
                            val wei = ethData.balance ?: 0.0
                            val ethAmount = wei / 1e18
                            result = WalletScanResult(
                                address = trimmed,
                                detectedChain = "Ethereum",
                                balance = String.format("%.4f", ethAmount),
                                balanceSymbol = "ETH",
                                txCount = ethData.transactionCount ?: 0,
                                explorerUrl = "https://blockchair.com/ethereum/address/$trimmed",
                                details = "استعلام زنده از Blockchair API: $ethAmount ETH"
                            )
                        }
                    }
                } catch (_: Exception) {}
            }

            // 3. Fallback to blockchain fallback scanner if Blockchair failed or for other chains
            if (result == null) {
                result = withContext(Dispatchers.IO) {
                    blockchainFallbackService.queryAddress(trimmed)
                }
            }

            scanResult.value = result

            // 4. Save to Room scan_history cache
            if (result != null) {
                scanHistoryDao.insertScan(
                    ScanHistoryEntity(
                        address = result.address,
                        blockchain = chain.name,
                        balance = result.balance,
                        balanceSymbol = result.balanceSymbol,
                        txCount = result.txCount,
                        cachedAt = System.currentTimeMillis(),
                        details = result.details
                    )
                )
            }

            isScanning.value = false
        }
    }

    fun loadPresetScannerAddress(preset: String) {
        val sample = when (preset) {
            "btc" -> "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa" // Satoshi Genesis
            "eth" -> "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045" // vitalik.eth
            "tron" -> "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t" // USDT TRC-20
            else -> "bc1qgdjqv0av3q56jvd82tkdjpy7gdp9ut8tlqmgrpmv24sq90ecnvqqjwvw97"
        }
        onScannerAddressChange(sample)
        checkAddressBalance(sample)
    }

    fun saveCurrentScanToWatchlist(customLabel: String = "") {
        val res = scanResult.value ?: return
        viewModelScope.launch {
            val label = customLabel.ifBlank { "آدرس ${res.detectedChain}" }
            trackedAddressDao.insert(
                TrackedAddressEntity(
                    label = label,
                    address = res.address,
                    blockchain = res.detectedChain,
                    lastKnownBalance = "${res.balance} ${res.balanceSymbol}",
                    status = if (res.balance != "0.0" && res.balance != "0") "دارای موجودی" else "در حال بررسی",
                    notes = "تعداد تراکنش‌ها: ${res.txCount}"
                )
            )
        }
    }

    fun deleteTrackedAddress(item: TrackedAddressEntity) {
        viewModelScope.launch {
            trackedAddressDao.delete(item)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            trackedAddressDao.clearAll()
            scanHistoryDao.clearAll()
        }
    }
}
