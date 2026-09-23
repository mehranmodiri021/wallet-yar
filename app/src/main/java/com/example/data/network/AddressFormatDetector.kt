package com.example.data.network

enum class Chain(val displayNameFa: String, val displayNameEn: String, val symbol: String) {
    BITCOIN("بیت‌کوین", "Bitcoin", "BTC"),
    ETHEREUM("اتریوم", "Ethereum", "ETH"),
    BNB_CHAIN("زنجیره هوشمند بایننس", "BNB Chain", "BNB"),
    TRON("ترون", "Tron", "TRX"),
    POLYGON("پالیگان", "Polygon", "POL"),
    SOLANA("سولانا", "Solana", "SOL"),
    UNKNOWN("نامشخص", "Unknown", "TOKEN")
}

enum class AddressFormat(
    val formatNameFa: String,
    val formatNameEn: String,
    val chain: Chain,
    val descriptionFa: String
) {
    BTC_LEGACY(
        formatNameFa = "لگسی (P2PKH)",
        formatNameEn = "Legacy (P2PKH)",
        chain = Chain.BITCOIN,
        descriptionFa = "آدرس سنتی بیت‌کوین که با ۱ شروع می‌شود. کارمزد بالاتر، استاندارد BIP-44."
    ),
    BTC_SEGWIT_NESTED(
        formatNameFa = "سگ‌ویت تو در تو (P2SH)",
        formatNameEn = "Nested SegWit (P2SH)",
        chain = Chain.BITCOIN,
        descriptionFa = "آدرس سازگار سگ‌ویت که با ۳ شروع می‌شود. استاندارد BIP-49."
    ),
    BTC_NATIVE_SEGWIT(
        formatNameFa = "سگ‌ویت خالص (Native SegWit Bech32)",
        formatNameEn = "Native SegWit (Bech32)",
        chain = Chain.BITCOIN,
        descriptionFa = "آدرس بهینه با شروع bc1q. کمترین کارمزد، استاندارد BIP-84."
    ),
    BTC_TAPROOT(
        formatNameFa = "تپ‌روت (Taproot Bech32m)",
        formatNameEn = "Taproot (Bech32m)",
        chain = Chain.BITCOIN,
        descriptionFa = "جدیدترین فرمت با شروع bc1p. حریم خصوصی بالا، استاندارد BIP-86."
    ),
    ETHEREUM_EVM(
        formatNameFa = "اتریوم و زنجیره‌های EVM",
        formatNameEn = "Ethereum & EVM",
        chain = Chain.ETHEREUM,
        descriptionFa = "فرمت ۴۲ کاراکتری هگزادسیمال با شروع 0x. سازگار با Polygon، Arbitrum و Optimism."
    ),
    BNB_SMART_CHAIN(
        formatNameFa = "بایننس اسمارت چین (BEP-20)",
        formatNameEn = "BNB Smart Chain (BEP-20)",
        chain = Chain.BNB_CHAIN,
        descriptionFa = "آدرس‌های شبکه BSC که با 0x آغاز می‌شوند."
    ),
    TRON_BASE58(
        formatNameFa = "ترون (TRC-20)",
        formatNameEn = "Tron (TRC-20)",
        chain = Chain.TRON,
        descriptionFa = "آدرس شبکه ترون با شروع حرف T و طول ۳۴ کاراکتر."
    ),
    SOLANA_BASE58(
        formatNameFa = "سولانا (Solana)",
        formatNameEn = "Solana (Base58)",
        chain = Chain.SOLANA,
        descriptionFa = "آدرس بر پایه Base58 با طول بین ۳۲ تا ۴۴ کاراکتر."
    ),
    UNKNOWN(
        formatNameFa = "فرمت ناشناخته",
        formatNameEn = "Unknown Format",
        chain = Chain.UNKNOWN,
        descriptionFa = "الگوی آدرس با شبکه‌های شناخته‌شده مطابقت ندارد."
    )
}

object AddressFormatDetector {
    private val HEX_CHARS = "0123456789abcdefABCDEF".toSet()
    private val BASE58_CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toSet()

    fun detectAddressFormat(address: String): AddressFormat {
        val trimmed = address.trim()
        if (trimmed.isEmpty()) return AddressFormat.UNKNOWN

        // Bitcoin Taproot (Bech32m) - starts with bc1p
        if (trimmed.startsWith("bc1p", ignoreCase = true) && trimmed.length in 58..64) {
            return AddressFormat.BTC_TAPROOT
        }

        // Bitcoin Native SegWit (Bech32) - starts with bc1q
        if (trimmed.startsWith("bc1q", ignoreCase = true) && trimmed.length in 42..62) {
            return AddressFormat.BTC_NATIVE_SEGWIT
        }

        // Bitcoin Legacy (1...)
        if (trimmed.startsWith("1") && trimmed.length in 26..35 && trimmed.all { it in BASE58_CHARS }) {
            return AddressFormat.BTC_LEGACY
        }

        // Bitcoin Nested SegWit (3...)
        if (trimmed.startsWith("3") && trimmed.length in 26..35 && trimmed.all { it in BASE58_CHARS }) {
            return AddressFormat.BTC_SEGWIT_NESTED
        }

        // Ethereum / EVM (0x...)
        if (trimmed.startsWith("0x") && trimmed.length == 42 && trimmed.drop(2).all { it in HEX_CHARS }) {
            return AddressFormat.ETHEREUM_EVM
        }

        // Tron (starts with T, length 34, base58)
        if (trimmed.startsWith("T") && trimmed.length == 34 && trimmed.all { it in BASE58_CHARS }) {
            return AddressFormat.TRON_BASE58
        }

        // Solana (32-44 base58 chars, doesn't start with 1 or 3 or T)
        if (trimmed.length in 32..44 && trimmed.all { it in BASE58_CHARS }) {
            return AddressFormat.SOLANA_BASE58
        }

        return AddressFormat.UNKNOWN
    }
}
