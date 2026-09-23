package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class DerivationInfo(
    val coinName: String,
    val standardName: String,
    val path: String,
    val addressPrefix: String,
    val usedBy: String,
    val tip: String
)

@Composable
fun DerivationPathScreen() {
    val clipboard = LocalClipboardManager.current
    var copiedPath by remember { mutableStateOf<String?>(null) }
    var selectedCoin by remember { mutableStateOf("All") }

    val derivationList = remember {
        listOf(
            DerivationInfo(
                coinName = "Bitcoin",
                standardName = "BIP-84 (Native SegWit)",
                path = "m/84'/0'/0'/0/0",
                addressPrefix = "bc1q...",
                usedBy = "Trust Wallet, Sparrow, BlueWallet, Ledger Live",
                tip = "کمترین کارمزد تراکنش را دارد. اگر قبلاً آدرستان با bc1 شروع می‌شده، این مسیر را انتخاب کنید."
            ),
            DerivationInfo(
                coinName = "Bitcoin",
                standardName = "BIP-49 (Nested SegWit)",
                path = "m/49'/0'/0'/0/0",
                addressPrefix = "3...",
                usedBy = "Trezor, Electrum, Exodus, پلتفرم‌های قدیمی‌تر",
                tip = "آدرس‌هایی که با عدد ۳ شروع می‌شوند از این مسیر اشتقاق استفاده می‌کنند."
            ),
            DerivationInfo(
                coinName = "Bitcoin",
                standardName = "BIP-44 (Legacy)",
                path = "m/44'/0'/0'/0/0",
                addressPrefix = "1...",
                usedBy = "کیف‌پول‌های قدیمی قبل از ۲۰۱۷، Bitcoin Core",
                tip = "اگر موجودی والت قدیمی شما صفر است، در کیف‌پول گزینه Legacy یا BIP44 را فعال کنید."
            ),
            DerivationInfo(
                coinName = "Bitcoin",
                standardName = "BIP-86 (Taproot)",
                path = "m/86'/0'/0'/0/0",
                addressPrefix = "bc1p...",
                usedBy = "Ordinals, Unisat, Xverse",
                tip = "مخصوص دارایی‌های تپ‌روت و Ordinals/BRC-20."
            ),
            DerivationInfo(
                coinName = "Ethereum",
                standardName = "BIP-44 (BIP-44 Standard EVM)",
                path = "m/44'/60'/0'/0/0",
                addressPrefix = "0x...",
                usedBy = "MetaMask, Trust Wallet, Rainbow, Coinbase Wallet",
                tip = "مسیر استاندارد اتریوم و تمام شبکه‌های سازگار با ماشین مجازی اتریوم (BSC, Polygon, Arbitrum)."
            ),
            DerivationInfo(
                coinName = "Ethereum",
                standardName = "Ledger Legacy (MEW / MyCrypto)",
                path = "m/44'/60'/0'/0",
                addressPrefix = "0x...",
                usedBy = "کیف پول‌های سخت‌افزاری لجر قبل از معرفی Ledger Live",
                tip = "اگر لجر قدیمی داشتید و در متامسک آدرس متفاوتی ظاهر می‌شود، نوع مسیر را به Ledger Legacy تغییر دهید."
            ),
            DerivationInfo(
                coinName = "Tron",
                standardName = "BIP-44 Tron",
                path = "m/44'/195'/0'/0/0",
                addressPrefix = "T...",
                usedBy = "TronLink, Trust Wallet",
                tip = "مخصوص رمزارز TRX و توکن‌های تتر USDT شبکه TRC20."
            ),
            DerivationInfo(
                coinName = "Solana",
                standardName = "BIP-44 / Deprecated Solana",
                path = "m/44'/501'/0'/0'",
                addressPrefix = "Base58 (32-44 کاراکتر)",
                usedBy = "Phantom, Solflare, Backpack",
                tip = "سولانا از منحنی ed25519 استفاده می‌کند؛ در فانتوم بررسی کنید گزینه derivation path روی standard باشد."
            )
        )
    }

    val filteredList = remember(selectedCoin) {
        if (selectedCoin == "All") derivationList
        else derivationList.filter { it.coinName == selectedCoin }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("derivation_path_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFB388FF).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFB388FF).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountTree, contentDescription = null, tint = Color(0xFFB388FF), modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "راهنمای مسیرهای اشتقاق (Derivation Paths)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "کلید حل معمای موجودی صفر پس از ریکاوری کلمات",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "یک عبارت ۱۲ یا ۲۴ کلمه‌ای می‌تواند میلیون‌ها آدرس مختلف بسازد! اگر کلمات را در والت جدید وارد کردید و موجودی صفر بود، به این معنی نیست که پولتان پریده؛ بلکه والت دارد مسیر اشتقاق دیگری را نشان می‌دهد.",
                        fontSize = 12.sp,
                        color = TextTertiary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Bitcoin", "Ethereum", "Tron", "Solana").forEach { coin ->
                    FilterChip(
                        selected = (selectedCoin == coin),
                        onClick = { selectedCoin = coin },
                        label = {
                            Text(
                                text = when (coin) {
                                    "All" -> "همه"
                                    "Bitcoin" -> "بیت‌کوین"
                                    "Ethereum" -> "اتریوم"
                                    "Tron" -> "ترون"
                                    "Solana" -> "سولانا"
                                    else -> coin
                                },
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }
        }

        // Derivation cards list
        items(filteredList) { item ->
            DerivationCard(
                info = item,
                onCopy = {
                    clipboard.setText(AnnotatedString(item.path))
                    copiedPath = item.path
                },
                isCopied = (copiedPath == item.path)
            )
        }

        // Step by step fix instructions
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CryptoSurfaceDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "دستورالعمل حل مشکل موجودی صفر در کیف‌پول‌ها:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                    Text(
                        text = "۱. کیف‌پول Electrum یا Sparrow را نصب کنید (این والت‌ها به شما اجازه می‌دهند نوع آدرس Legacy یا SegWit را دستی انتخاب کنید).",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                    Text(
                        text = "۲. هنگام ایمپورت کلمات، گزینه Options و سپس انتخاب مسیر Derivation Path را تیک بزنید.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                    Text(
                        text = "۳. مسیرهای m/44'/0'/0'/0/0 و m/49'/0'/0'/0/0 و m/84'/0'/0'/0/0 را تست کنید تا آدرس قدیمی شما پیدا شود.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun DerivationCard(
    info: DerivationInfo,
    onCopy: () -> Unit,
    isCopied: Boolean
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CryptoBorderDark, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${info.coinName} - ${info.standardName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = info.addressPrefix,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyanPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Surface(
                color = CryptoSurfaceDark,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = info.path,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = GoldAccent
                    )
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "کپی مسیر",
                            tint = if (isCopied) GreenSuccess else TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = "کیف‌پول‌های معمول: ${info.usedBy}",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Text(
                text = info.tip,
                fontSize = 12.sp,
                color = TextTertiary,
                lineHeight = 16.sp
            )
        }
    }
}
