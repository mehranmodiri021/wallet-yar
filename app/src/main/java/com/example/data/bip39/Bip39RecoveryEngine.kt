package com.example.data.bip39

import java.security.MessageDigest

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val invalidWordIndices: List<Pair<Int, String>> = emptyList()
)

data class RecoveryCandidate(
    val word: String,
    val fullPhrase: List<String>,
    val wordIndex: Int
)

data class MultiRecoveryCandidate(
    val recoveredWords: Map<Int, String>, // index -> word
    val fullPhrase: List<String>
)

object Bip39RecoveryEngine {

    fun validatePhrase(words: List<String>): ValidationResult {
        if (words.size != 12) {
            return ValidationResult(
                isValid = false,
                errorMessage = "تعداد کلمات باید دقیقاً ۱۲ کلمه باشد (در حال حاضر: ${words.size})"
            )
        }

        val invalidWords = mutableListOf<Pair<Int, String>>()
        for (i in words.indices) {
            val w = words[i].trim().lowercase()
            if (!Bip39WordList.isValidWord(w)) {
                invalidWords.add(i to words[i])
            }
        }

        if (invalidWords.isNotEmpty()) {
            return ValidationResult(
                isValid = false,
                errorMessage = "برخی کلمات در دیکشنری استاندارد BIP-39 یافت نشدند",
                invalidWordIndices = invalidWords
            )
        }

        val indices = words.map { Bip39WordList.wordToIndex[it.trim().lowercase()]!! }
        val checksumOk = verifyChecksum(indices)

        return if (checksumOk) {
            ValidationResult(isValid = true)
        } else {
            ValidationResult(
                isValid = false,
                errorMessage = "چک‌سام (Checksum) نامعتبر است. ترتیب کلمات یا املای آن‌ها اشتباه است."
            )
        }
    }

    /**
     * Recovery for single missing word
     */
    fun findMissingWordCandidates(
        words: List<String>,
        missingIndex: Int,
        prefixHint: String = ""
    ): List<RecoveryCandidate> {
        if (words.size != 12 || missingIndex !in 0..11) return emptyList()

        val candidateWords = if (prefixHint.isNotBlank()) {
            Bip39WordList.words.filter { it.startsWith(prefixHint.trim().lowercase()) }
        } else {
            Bip39WordList.words
        }

        val knownIndices = words.map { word ->
            Bip39WordList.wordToIndex[word.trim().lowercase()] ?: -1
        }.toMutableList()

        val results = mutableListOf<RecoveryCandidate>()

        for (candidate in candidateWords) {
            val candidateIndex = Bip39WordList.wordToIndex[candidate] ?: continue
            knownIndices[missingIndex] = candidateIndex

            if (verifyChecksum(knownIndices)) {
                val fullPhrase = words.toMutableList()
                fullPhrase[missingIndex] = candidate
                results.add(RecoveryCandidate(candidate, fullPhrase, missingIndex))
            }
        }

        return results
    }

    /**
     * Multi-word recovery supporting 1, 2, or 3 missing words!
     * For 3 missing words: with optional prefix/length hints, or targeted search with maxResults.
     * Calculates mathematically valid combinations satisfying the 4-bit SHA-256 checksum.
     */
    fun findMultiMissingWordCandidates(
        words: List<String>,
        missingIndices: List<Int>,
        hints: Map<Int, String> = emptyMap(),
        maxResults: Int = 100,
        onProgress: ((testedCount: Int, foundCount: Int) -> Unit)? = null
    ): List<MultiRecoveryCandidate> {
        if (words.size != 12 || missingIndices.isEmpty() || missingIndices.size > 3) return emptyList()

        val sortedMissing = missingIndices.distinct().sorted()
        val currentWords = words.toMutableList()

        // Prepare candidate pool per missing index
        val candidatePools = sortedMissing.map { idx ->
            val hint = hints[idx]?.trim()?.lowercase() ?: ""
            if (hint.isNotBlank()) {
                val filtered = Bip39WordList.words.filter { it.startsWith(hint) }
                if (filtered.isNotEmpty()) filtered else Bip39WordList.words
            } else {
                Bip39WordList.words
            }
        }

        val knownIndices = currentWords.map { word ->
            Bip39WordList.wordToIndex[word.trim().lowercase()] ?: -1
        }.toMutableList()

        val results = mutableListOf<MultiRecoveryCandidate>()
        var tested = 0

        when (sortedMissing.size) {
            1 -> {
                val m0 = sortedMissing[0]
                val pool0 = candidatePools[0]
                for (w0 in pool0) {
                    knownIndices[m0] = Bip39WordList.wordToIndex[w0] ?: continue
                    if (verifyChecksum(knownIndices)) {
                        val phrase = currentWords.toMutableList()
                        phrase[m0] = w0
                        results.add(MultiRecoveryCandidate(mapOf(m0 to w0), phrase))
                        if (results.size >= maxResults) break
                    }
                }
            }
            2 -> {
                val m0 = sortedMissing[0]
                val m1 = sortedMissing[1]
                val pool0 = candidatePools[0]
                val pool1 = candidatePools[1]

                for (w0 in pool0) {
                    knownIndices[m0] = Bip39WordList.wordToIndex[w0] ?: continue
                    for (w1 in pool1) {
                        knownIndices[m1] = Bip39WordList.wordToIndex[w1] ?: continue
                        tested++
                        if (verifyChecksum(knownIndices)) {
                            val phrase = currentWords.toMutableList()
                            phrase[m0] = w0
                            phrase[m1] = w1
                            results.add(MultiRecoveryCandidate(mapOf(m0 to w0, m1 to w1), phrase))
                            if (results.size >= maxResults) break
                        }
                    }
                    if (tested % 20000 == 0) {
                        onProgress?.invoke(tested, results.size)
                    }
                    if (results.size >= maxResults) break
                }
            }
            3 -> {
                val m0 = sortedMissing[0]
                val m1 = sortedMissing[1]
                val m2 = sortedMissing[2]
                val pool0 = candidatePools[0]
                val pool1 = candidatePools[1]
                val pool2 = candidatePools[2]

                // For 3 words, search through pools with periodic progress
                for (w0 in pool0) {
                    knownIndices[m0] = Bip39WordList.wordToIndex[w0] ?: continue
                    for (w1 in pool1) {
                        knownIndices[m1] = Bip39WordList.wordToIndex[w1] ?: continue
                        for (w2 in pool2) {
                            knownIndices[m2] = Bip39WordList.wordToIndex[w2] ?: continue
                            tested++
                            if (verifyChecksum(knownIndices)) {
                                val phrase = currentWords.toMutableList()
                                phrase[m0] = w0
                                phrase[m1] = w1
                                phrase[m2] = w2
                                results.add(MultiRecoveryCandidate(mapOf(m0 to w0, m1 to w1, m2 to w2), phrase))
                                if (results.size >= maxResults) break
                            }
                        }
                        if (results.size >= maxResults) break
                    }
                    if (tested % 50000 == 0) {
                        onProgress?.invoke(tested, results.size)
                    }
                    if (results.size >= maxResults) break
                }
            }
        }

        return results
    }

    fun verifyChecksum(indices: List<Int>): Boolean {
        if (indices.size != 12) return false
        val bits = BooleanArray(132)
        for (i in 0 until 12) {
            val idx = indices[i]
            if (idx < 0 || idx >= 2048) return false
            for (b in 0 until 11) {
                bits[i * 11 + b] = ((idx ushr (10 - b)) and 1) == 1
            }
        }

        val entropy = ByteArray(16)
        for (byteIdx in 0 until 16) {
            var v = 0
            for (bit in 0 until 8) {
                if (bits[byteIdx * 8 + bit]) {
                    v = v or (1 shl (7 - bit))
                }
            }
            entropy[byteIdx] = v.toByte()
        }

        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(entropy)
        val expectedChecksum = (hash[0].toInt() and 0xFF) ushr 4

        var actualChecksum = 0
        for (bit in 0 until 4) {
            if (bits[128 + bit]) {
                actualChecksum = actualChecksum or (1 shl (3 - bit))
            }
        }

        return expectedChecksum == actualChecksum
    }
}
