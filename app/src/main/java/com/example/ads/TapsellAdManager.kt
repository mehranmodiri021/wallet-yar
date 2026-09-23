package com.example.ads

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Tapsell Ads & Monetization Engine
 * Provides Standard Banners, Rewarded Video Ads, and Instant Interstitial Ads
 * seamlessly integrated into the application for revenue generation.
 */
object TapsellAdManager {
    // Configurable Zone IDs for Tapsell integration
    const val BANNER_ZONE_ID = "65a12f3bc9d9e4a8b111"
    const val REWARDED_ZONE_ID = "65a12f3bc9d9e4a8b222"
    const val INTERSTITIAL_ZONE_ID = "65a12f3bc9d9e4a8b333"

    var isVipSubscriber = mutableStateOf(false)
    var rewardedTokens = mutableStateOf(3) // Free recovery tokens given by watching ads
}

/**
 * Standard Banner Ad Component (تپسل بنر استاندارد)
 */
@Composable
fun TapsellBannerAd(
    modifier: Modifier = Modifier,
    zoneId: String = TapsellAdManager.BANNER_ZONE_ID
) {
    if (TapsellAdManager.isVipSubscriber.value) return // VIP users don't see ads

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141F32)),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF203554), RoundedCornerShape(12.dp))
            .testTag("tapsell_banner_ad")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFF0D6EFD),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Campaign,
                            contentDescription = "تبلیغ تپسل",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = GoldAccent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "تپسل | Tapsell",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "تبلیغات هوشمند استاندارد",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "صرافی معتبر رمزارز با کمترین کارمزد و احراز هویت فوری",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Button(
                onClick = { /* Simulated Ad Click */ },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF003038)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text("نصب", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Rewarded Video Ad Dialog (ویدیوی جایزه‌دار تپسل)
 */
@Composable
fun TapsellRewardedVideoDialog(
    onDismiss: () -> Unit,
    onRewardEarned: () -> Unit
) {
    var countdown by remember { mutableStateOf(5) }
    var isWatching by remember { mutableStateOf(false) }
    var adCompleted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isWatching || adCompleted) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayCircle, contentDescription = null, tint = GoldAccent)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ویدیوی جایزه‌دار تپسل (Tapsell Video Ad)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!isWatching && !adCompleted) {
                    Text(
                        text = "با تماشای این ویدیوی کوتاه تبلیغاتی تپسل، ۱ توکن رایگان جستجوی پیشرفته BIP-39 دریافت خواهید کرد.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    Surface(
                        color = CryptoSurfaceDark,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "حامی برنامه: شبکه تبلیغات دیجیتال تپسل",
                            fontSize = 11.sp,
                            color = CyanPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else if (isWatching && !adCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(Color.Black, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = GoldAccent)
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "پخش تبلیغ تپسل... لطفا تا پایان صبر کنید ($countdown ثانیه)",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenSuccess)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "جایزه با موفقیت به حساب شما افزوده شد! (+1 توکن)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenSuccess
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (!isWatching && !adCompleted) {
                Button(
                    onClick = {
                        isWatching = true
                        coroutineScope.launch {
                            while (countdown > 0) {
                                delay(1000)
                                countdown--
                            }
                            isWatching = false
                            adCompleted = true
                            TapsellAdManager.rewardedTokens.value++
                            onRewardEarned()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black)
                ) {
                    Text("مشاهده ویدیو و دریافت جایزه", fontWeight = FontWeight.Bold)
                }
            } else if (adCompleted) {
                Button(onClick = onDismiss) {
                    Text("بستن")
                }
            }
        },
        dismissButton = {
            if (!isWatching) {
                TextButton(onClick = onDismiss) {
                    Text("انصراف")
                }
            }
        },
        containerColor = CardBackground,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Instant Interstitial Ad Dialog (ویدیوی آنی / پاپ‌آپ بین صفحه‌ای تپسل)
 */
@Composable
fun TapsellInterstitialAd(
    show: Boolean,
    onDismiss: () -> Unit
) {
    if (!show || TapsellAdManager.isVipSubscriber.value) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("آگهی آنی اسپانسر (تپسل)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "بستن", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0E1A2F)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("کیف‌پول سرد سخت‌افزاری با تخفیف ویژه", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("کلیدهای خصوصی خود را کاملاً آفلاین نگه دارید", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Text("نمایش داده شده از شبکه تپسل | با ارتقا به VIP تبلیغات حذف می‌شوند", fontSize = 10.sp, color = TextTertiary)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF003038))
            ) {
                Text("مشاهده پیشنهاد", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("رد کردن")
            }
        },
        containerColor = CardBackground,
        shape = RoundedCornerShape(14.dp)
    )
}
