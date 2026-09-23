package com.example.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.TimeUnit

data class WalletScanResult(
    val address: String,
    val detectedChain: String,
    val balance: String,
    val balanceSymbol: String,
    val txCount: Int = 0,
    val explorerUrl: String = "",
    val details: String = "",
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)

class BlockchainApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    fun detectAddressType(address: String): String {
        val trimmed = address.trim()
        return when {
            trimmed.startsWith("bc1") || trimmed.startsWith("1") || trimmed.startsWith("3") -> "Bitcoin"
            trimmed.startsWith("0x") && trimmed.length == 42 -> "Ethereum / EVM"
            trimmed.startsWith("T") && trimmed.length == 34 -> "Tron"
            trimmed.length in 32..44 && !trimmed.startsWith("0x") -> "Solana / Alt"
            else -> "Unknown"
        }
    }

    suspend fun queryAddress(address: String): WalletScanResult = withContext(Dispatchers.IO) {
        val cleanAddress = address.trim()
        val chain = detectAddressType(cleanAddress)

        when (chain) {
            "Bitcoin" -> queryBitcoin(cleanAddress)
            "Ethereum / EVM" -> queryEthereum(cleanAddress)
            "Tron" -> queryTron(cleanAddress)
            else -> queryGenericFallback(cleanAddress)
        }
    }

    private fun queryBitcoin(address: String): WalletScanResult {
        return try {
            // First attempt: Blockstream API
            val request = Request.Builder()
                .url("https://blockstream.info/api/address/$address")
                .header("User-Agent", "CryptoRecovery/1.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val json = JSONObject(body)
                    val chainStats = json.optJSONObject("chain_stats")
                    val funded = chainStats?.optLong("funded_txo_sum", 0L) ?: 0L
                    val spent = chainStats?.optLong("spent_txo_sum", 0L) ?: 0L
                    val txCount = chainStats?.optInt("tx_count", 0) ?: 0
                    val satoshis = funded - spent
                    val btc = satoshis.toDouble() / 100_000_000.0

                    WalletScanResult(
                        address = address,
                        detectedChain = "Bitcoin (BTC)",
                        balance = "%.8f".format(btc),
                        balanceSymbol = "BTC",
                        txCount = txCount,
                        explorerUrl = "https://mempool.space/address/$address",
                        details = "مجموع دریافت شده: %.8f BTC | تراکنش‌ها: $txCount".format(funded.toDouble() / 100_000_000.0)
                    )
                } else {
                    // Fallback to blockchain.info
                    queryBitcoinBlockchainInfo(address)
                }
            }
        } catch (e: Exception) {
            queryBitcoinBlockchainInfo(address)
        }
    }

    private fun queryBitcoinBlockchainInfo(address: String): WalletScanResult {
        return try {
            val request = Request.Builder()
                .url("https://blockchain.info/rawaddr/$address?limit=1")
                .header("User-Agent", "CryptoRecovery/1.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val json = JSONObject(body)
                    val satoshis = json.optLong("final_balance", 0L)
                    val txCount = json.optInt("n_tx", 0)
                    val btc = satoshis.toDouble() / 100_000_000.0
                    val totalReceived = json.optLong("total_received", 0L).toDouble() / 100_000_000.0

                    WalletScanResult(
                        address = address,
                        detectedChain = "Bitcoin (BTC)",
                        balance = "%.8f".format(btc),
                        balanceSymbol = "BTC",
                        txCount = txCount,
                        explorerUrl = "https://www.blockchain.com/explorer/addresses/btc/$address",
                        details = "کل دریافتی: %.8f BTC | تعداد تراکنش: $txCount".format(totalReceived)
                    )
                } else {
                    WalletScanResult(
                        address = address,
                        detectedChain = "Bitcoin (BTC)",
                        balance = "0.00000000",
                        balanceSymbol = "BTC",
                        explorerUrl = "https://mempool.space/address/$address",
                        isSuccess = false,
                        errorMessage = "پاسخ از سرور بیت‌کوین دریافت نشد (${response.code})"
                    )
                }
            }
        } catch (e: Exception) {
            WalletScanResult(
                address = address,
                detectedChain = "Bitcoin (BTC)",
                balance = "0.00000000",
                balanceSymbol = "BTC",
                explorerUrl = "https://mempool.space/address/$address",
                isSuccess = false,
                errorMessage = "خطا در اتصال به شبکه یا استعلام آدرس: ${e.localizedMessage ?: "عدم اتصال"}"
            )
        }
    }

    private fun queryEthereum(address: String): WalletScanResult {
        return try {
            // Use public Cloudflare Ethereum JSON-RPC endpoint
            val jsonBody = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("method", "eth_getBalance")
                put("params", org.json.JSONArray().apply {
                    put(address)
                    put("latest")
                })
                put("id", 1)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://cloudflare-eth.com")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val json = JSONObject(body)
                    val resultHex = json.optString("result", "0x0")
                    val cleanHex = if (resultHex.startsWith("0x")) resultHex.substring(2) else resultHex
                    val wei = if (cleanHex.isNotBlank()) BigInteger(cleanHex, 16) else BigInteger.ZERO
                    val eth = BigDecimal(wei).divide(BigDecimal("1000000000000000000"))

                    WalletScanResult(
                        address = address,
                        detectedChain = "Ethereum / EVM (ETH, BSC, Polygon)",
                        balance = "%.6f".format(eth.toDouble()),
                        balanceSymbol = "ETH",
                        explorerUrl = "https://etherscan.io/address/$address",
                        details = "موجودی شبکه اصلی اتریوم. برای بررسی توکن‌های ERC20 و سایر شبکه‌ها به اکسپلورر مراجعه فرمایید."
                    )
                } else {
                    WalletScanResult(
                        address = address,
                        detectedChain = "Ethereum / EVM",
                        balance = "0.000000",
                        balanceSymbol = "ETH",
                        explorerUrl = "https://etherscan.io/address/$address",
                        isSuccess = false,
                        errorMessage = "کد پاسخ نود اتریوم: ${response.code}"
                    )
                }
            }
        } catch (e: Exception) {
            WalletScanResult(
                address = address,
                detectedChain = "Ethereum / EVM",
                balance = "0.000000",
                balanceSymbol = "ETH",
                explorerUrl = "https://etherscan.io/address/$address",
                isSuccess = false,
                errorMessage = "خطای اتصال به نود اتریوم: ${e.localizedMessage ?: "محدودیت شبکه"}"
            )
        }
    }

    private fun queryTron(address: String): WalletScanResult {
        return try {
            val request = Request.Builder()
                .url("https://api.trongrid.io/v1/accounts/$address")
                .header("User-Agent", "CryptoRecovery/1.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val json = JSONObject(body)
                    val dataArray = json.optJSONArray("data")
                    val sunBalance = if (dataArray != null && dataArray.length() > 0) {
                        dataArray.getJSONObject(0).optLong("balance", 0L)
                    } else 0L

                    val trx = sunBalance.toDouble() / 1_000_000.0

                    WalletScanResult(
                        address = address,
                        detectedChain = "Tron (TRX / TRC-20)",
                        balance = "%.4f".format(trx),
                        balanceSymbol = "TRX",
                        explorerUrl = "https://tronscan.org/#/address/$address",
                        details = "موجودی شبکه ترون. جهت بررسی توکن‌های تتر TRC-20 پیوند اکسپلورر را مشاهده کنید."
                    )
                } else {
                    WalletScanResult(
                        address = address,
                        detectedChain = "Tron (TRX)",
                        balance = "0.0000",
                        balanceSymbol = "TRX",
                        explorerUrl = "https://tronscan.org/#/address/$address",
                        isSuccess = false,
                        errorMessage = "پاسخ ناموفق از شبکه ترون (${response.code})"
                    )
                }
            }
        } catch (e: Exception) {
            WalletScanResult(
                address = address,
                detectedChain = "Tron (TRX)",
                balance = "0.0000",
                balanceSymbol = "TRX",
                explorerUrl = "https://tronscan.org/#/address/$address",
                isSuccess = false,
                errorMessage = "خطا در شبکه ترون: ${e.localizedMessage}"
            )
        }
    }

    private fun queryGenericFallback(address: String): WalletScanResult {
        return WalletScanResult(
            address = address,
            detectedChain = "سایر زنجیره‌ها (Solana, Cosmos, etc.)",
            balance = "نامشخص",
            balanceSymbol = "ASSET",
            explorerUrl = "https://blockchair.com/search?q=$address",
            details = "فرمت آدرس به عنوان زنجیره مستقل یا آدرس عمومی شناسایی شد.",
            isSuccess = true
        )
    }
}
