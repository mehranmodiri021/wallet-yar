package com.example

import com.example.data.bip39.Bip39RecoveryEngine
import com.example.data.bip39.Bip39WordList
import com.example.data.network.AddressFormat
import com.example.data.network.AddressFormatDetector
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testBip39WordList() {
    assertTrue("Wordlist should contain 2048 words", Bip39WordList.words.size == 2048)
    assertTrue("Wordlist should have 'abandon'", Bip39WordList.isValidWord("abandon"))
    assertTrue("Wordlist should have 'zoo'", Bip39WordList.isValidWord("zoo"))
    assertFalse("Wordlist should reject invalid word", Bip39WordList.isValidWord("fakewordxyz"))
  }

  @Test
  fun testBip39ValidationAndRecovery() {
    val sampleValid = listOf(
      "abandon", "abandon", "abandon", "abandon", "abandon", "abandon",
      "abandon", "abandon", "abandon", "abandon", "abandon", "about"
    )
    val validation = Bip39RecoveryEngine.validatePhrase(sampleValid)
    assertTrue("Known valid test vector should be valid", validation.isValid)

    // Test missing 1 word
    val partial = sampleValid.toMutableList()
    partial[11] = ""
    val candidates = Bip39RecoveryEngine.findMissingWordCandidates(partial, 11)
    assertTrue("Should find valid candidates", candidates.isNotEmpty())
    assertTrue("Candidates should contain 'about'", candidates.any { it.word == "about" })

    // Test multi-word recovery with 2 missing words
    val partial2 = sampleValid.toMutableList()
    partial2[10] = ""
    partial2[11] = ""
    val candidates2 = Bip39RecoveryEngine.findMultiMissingWordCandidates(
      words = partial2,
      missingIndices = listOf(10, 11),
      hints = mapOf(10 to "ab", 11 to "ab"),
      maxResults = 20
    )
    assertTrue("Should find valid multi candidates", candidates2.isNotEmpty())
    assertTrue(
      "Should contain exact match",
      candidates2.any { it.recoveredWords[10] == "abandon" && it.recoveredWords[11] == "about" }
    )

    // Test multi-word recovery with 3 missing words using prefix hints
    val partial3 = sampleValid.toMutableList()
    partial3[9] = ""
    partial3[10] = ""
    partial3[11] = ""
    val candidates3 = Bip39RecoveryEngine.findMultiMissingWordCandidates(
      words = partial3,
      missingIndices = listOf(9, 10, 11),
      hints = mapOf(9 to "aba", 10 to "aba", 11 to "abo"),
      maxResults = 10
    )
    assertTrue("Should find valid 3-word candidates with hints", candidates3.isNotEmpty())
  }

  @Test
  fun testSha256ChecksumVerification() {
    val sampleValid = listOf(
      "abandon", "abandon", "abandon", "abandon", "abandon", "abandon",
      "abandon", "abandon", "abandon", "abandon", "abandon", "about"
    )
    val indices = sampleValid.map { Bip39WordList.wordToIndex[it]!! }
    assertTrue("SHA-256 checksum check must pass for valid vector", Bip39RecoveryEngine.verifyChecksum(indices))

    val invalidIndices = indices.toMutableList()
    invalidIndices[11] = 0 // "abandon" as 12th word produces invalid 4-bit checksum
    assertFalse("SHA-256 checksum check must fail for invalid vector", Bip39RecoveryEngine.verifyChecksum(invalidIndices))
  }

  @Test
  fun testAddressFormatDetection() {
    // Bitcoin Legacy (1...)
    assertEquals(
      AddressFormat.BTC_LEGACY,
      AddressFormatDetector.detectAddressFormat("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa")
    )

    // Bitcoin Nested SegWit (3...)
    assertEquals(
      AddressFormat.BTC_SEGWIT_NESTED,
      AddressFormatDetector.detectAddressFormat("3J98t1WpEZ73CNmQviecrnyiWrnqRhWNLy")
    )

    // Bitcoin Native SegWit (bc1q...)
    assertEquals(
      AddressFormat.BTC_NATIVE_SEGWIT,
      AddressFormatDetector.detectAddressFormat("bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq")
    )

    // Bitcoin Taproot (bc1p...)
    assertEquals(
      AddressFormat.BTC_TAPROOT,
      AddressFormatDetector.detectAddressFormat("bc1p5d7rjq7g6rd22kyhv0x5qvqdwtvnvt5etpfgtjj5z7ur0unfv2fq5073ba")
    )

    // Ethereum (0x...)
    assertEquals(
      AddressFormat.ETHEREUM_EVM,
      AddressFormatDetector.detectAddressFormat("0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045")
    )

    // Tron (T...)
    assertEquals(
      AddressFormat.TRON_BASE58,
      AddressFormatDetector.detectAddressFormat("TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t")
    )
  }
}
