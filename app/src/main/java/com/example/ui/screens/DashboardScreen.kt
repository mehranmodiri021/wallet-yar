package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.TapsellBannerAd
import com.example.ui.WalletRecoveryViewModel
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: WalletRecoveryViewModel,
    onNavigate: (Int) -> Unit
) {
    val trackedAddresses by viewModel.trackedAddresses.collectAsState()
    val scans by viewModel.scanHistory.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tapsell Banner Ad
        item {
            TapsellBannerAd()
        }

        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "کیف پول یار (Wallet Yar)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "جعبه‌ابزار اخلاقی و تخصصی بازیابی دارایی دیجیتال",
                                fontSize = 11.sp,
                                color = CyanPrimary
                            )
                        }

                        Surface(
                            color = GreenSuccess.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Text(
                        text = "محاسبات آفلاین در RAM بدون دسترسی به اینترنت برای عبارات بازیابی | پایگاه داده ۲۰۴۸ کلمه‌ای BIP-39 | استعلام رسمی از کاوشگر بلاک‌چین",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Summary Cards: Scans & Watchlist
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    title = "آدرس‌های واچ‌لیست",
                    count = "${trackedAddresses.size}",
                    icon = Icons.Default.Bookmarks,
                    color = CyanPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(5) } // Watchlist
                )

                SummaryCard(
                    title = "تاریخچه استعلام‌ها",
                    count = "${scans.size}",
                    icon = Icons.Default.History,
                    color = GoldAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(3) } // Balance Scanner
                )
            }
        }

        // Emergency Callout
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF381518)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RedAlert, RoundedCornerShape(14.dp))
                    .clickable { onNavigate(7) } // Emergency
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = RedAlert, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "🚨 پروتکل اضطراری (اگر Seed شما لو رفته)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "انتقال دارایی، جلوگیری از ربات‌های جاروب و فرم پلیس فتا",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = RedAlert)
                }
            }
        }

        // Quick Tools Grid Title
        item {
            Text(
                text = "جعبه‌ابزارهای تخصصی:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Quick Tool Items
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickToolItem(
                    title = "اعتبارسنجی عبارات ۱۲ و ۲۴ کلمه‌ای (Seed Validator)",
                    description = "بررسی زنده ۲۰۴۸ کلمه دیکشنری و چک‌سام SHA-256",
                    icon = Icons.AutoMirrored.Filled.FactCheck,
                    color = CyanPrimary,
                    onClick = { onNavigate(1) } // Seed Validator
                )

                QuickToolItem(
                    title = "کشف کلمات مفقود و ناخوانا (Missing Words)",
                    description = "جستجوی هوشمند برای ۱ تا ۳ کلمه مفقود با فیلتر پیشوند",
                    icon = Icons.Default.Psychology,
                    color = GoldAccent,
                    onClick = { onNavigate(2) } // Missing Words
                )

                QuickToolItem(
                    title = "استعلام زنده موجودی و تراکنش‌ها (Balance Scanner)",
                    description = "پشتیبانی از Bitcoin، Ethereum، Tron و کش ۲۴ ساعته",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = GreenSuccess,
                    onClick = { onNavigate(3) } // Balance Scanner
                )

                QuickToolItem(
                    title = "مسیرهای اشتقاق کلید (Derivation Paths BIP-44/49/84/86)",
                    description = "فرمت‌های P2PKH، SegWit و Taproot برای متامسک، اکسودوس و لجر",
                    icon = Icons.Default.AccountTree,
                    color = Color(0xFF64B5F6),
                    onClick = { onNavigate(4) } // Derivation
                )

                QuickToolItem(
                    title = "راهنمای گام‌به‌گام بازیابی ولت‌ها (Recovery Guides)",
                    description = "آموزش متامسک، تراست ولت، لجر، فانتوم و اکسودوس",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    color = Color(0xFFFFB74D),
                    onClick = { onNavigate(6) } // Guides
                )

                QuickToolItem(
                    title = "شناسایی کلاهبرداری‌های بازیابی (Scam Alert)",
                    description = "لیست سیاه آدرس‌های مشکوک و هشدارهای پیشگیری از سرقت",
                    icon = Icons.Default.Shield,
                    color = RedAlert,
                    onClick = { onNavigate(8) } // Scam Alert
                )
            }
        }

        item {
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = TextSecondary)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Text(count, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun QuickToolItem(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CryptoBorderDark, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(2.dp))
                Text(description, fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)
            }
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextTertiary)
        }
    }
}
