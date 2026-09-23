package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.TapsellAdManager
import com.example.ads.TapsellBannerAd
import com.example.ads.TapsellRewardedVideoDialog
import com.example.billing.BazaarVipSubscriptionDialog
import com.example.billing.CafeBazaarBillingManager
import com.example.settings.AppLanguage
import com.example.settings.AppSettingsState
import com.example.settings.ColorThemeMode
import com.example.ui.WalletRecoveryViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(viewModel: WalletRecoveryViewModel) {
    val scrollState = rememberScrollState()
    val isDark = AppSettingsState.isDarkMode.value
    val currentLang = AppSettingsState.currentLanguage.value
    val currentTheme = AppSettingsState.currentThemeMode.value
    val fontScale = AppSettingsState.fontScaleFactor.floatValue
    val isVip = CafeBazaarBillingManager.isSubscribed.value
    val context = LocalContext.current

    var showVipDialog by remember { mutableStateOf(false) }
    var showRewardedAdDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    if (showVipDialog) {
        BazaarVipSubscriptionDialog(
            onDismiss = { showVipDialog = false },
            onSuccess = { showVipDialog = false }
        )
    }

    if (showRewardedAdDialog) {
        TapsellRewardedVideoDialog(
            onDismiss = { showRewardedAdDialog = false },
            onRewardEarned = { showRewardedAdDialog = false }
        )
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("پاک‌سازی تمام داده‌ها") },
            text = { Text("آیا مطمئن هستید؟ با این کار تاریخچه اسکن‌ها و آدرس‌های واچ‌لیست پاک خواهند شد.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                        Toast.makeText(context, "تمامی داده‌های محلی پاک‌سازی شدند", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedAlert)
                ) {
                    Text("بله، پاک کن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen")
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tapsell Banner Ad
        TapsellBannerAd()

        // VIP Subscription Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isVip) Color(0xFF1B3B2B) else Color(0xFF2C2413)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (isVip) GreenSuccess else GoldAccent,
                    RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        color = if (isVip) GreenSuccess.copy(alpha = 0.2f) else GoldAccent.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = if (isVip) GreenSuccess else GoldAccent
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isVip) "حساب طلایی VIP کافه‌بازار فعال است" else "حساب VIP کیف پول یار",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isVip) "دسترسی نامحدود و بدون تبلیغات" else "حذف کامل تبلیغات و استعلام فوق‌سریع",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                if (!isVip) {
                    Button(
                        onClick = { showVipDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("ارتقا", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Rewarded Video Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = CyanPrimary)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("توکن‌های جستجوی پیشرفته", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("موجودی توکن: ${TapsellAdManager.rewardedTokens.value} (تماشای ویدیوی جایزه‌دار)", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                OutlinedButton(
                    onClick = { showRewardedAdDialog = true },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("تماشای تبلیغ", fontSize = 11.sp)
                }
            }
        }

        // Appearance Settings
        Text(
            text = "شخصی‌سازی و ظاهر",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyanPrimary
        )

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Dark Mode Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = GoldAccent
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("حالت تاریک / شب", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(if (isDark) "حالت تیره فعال است" else "حالت روشن فعال است", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    Switch(
                        checked = isDark,
                        onCheckedChange = { AppSettingsState.isDarkMode.value = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyanPrimary)
                    )
                }

                HorizontalDivider(color = CryptoBorderDark)

                // Color Themes
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("پالت رنگی برنامه:", fontSize = 12.sp, color = TextSecondary)
                    ColorThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (currentTheme == mode) CryptoSurfaceDark else Color.Transparent)
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(mode.title, fontSize = 12.sp, color = if (currentTheme == mode) CyanPrimary else Color.White)
                            RadioButton(
                                selected = (currentTheme == mode),
                                onClick = { AppSettingsState.currentThemeMode.value = mode }
                            )
                        }
                    }
                }

                HorizontalDivider(color = CryptoBorderDark)

                // Language
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = CyanPrimary)
                        Spacer(Modifier.width(10.dp))
                        Text("زبان (Language)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = currentLang == AppLanguage.PERSIAN,
                            onClick = { AppSettingsState.currentLanguage.value = AppLanguage.PERSIAN },
                            label = { Text("فارسی", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = currentLang == AppLanguage.ENGLISH,
                            onClick = { AppSettingsState.currentLanguage.value = AppLanguage.ENGLISH },
                            label = { Text("English", fontSize = 11.sp) }
                        )
                    }
                }

                HorizontalDivider(color = CryptoBorderDark)

                // Font Size Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatSize, contentDescription = null, tint = TextSecondary)
                            Spacer(Modifier.width(8.dp))
                            Text("اندازه متون:", fontSize = 12.sp, color = Color.White)
                        }
                        Text(
                            text = when {
                                fontScale < 0.95f -> "کوچک"
                                fontScale > 1.15f -> "بزرگ"
                                else -> "استاندارد"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                    }

                    Slider(
                        value = fontScale,
                        onValueChange = { AppSettingsState.fontScaleFactor.floatValue = it },
                        valueRange = 0.85f..1.30f,
                        steps = 3,
                        colors = SliderDefaults.colors(thumbColor = CyanPrimary, activeTrackColor = CyanPrimary)
                    )
                }

                HorizontalDivider(color = CryptoBorderDark)

                // Clear all data button
                OutlinedButton(
                    onClick = { showClearDataDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedAlert),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("پاک کردن همه داده‌ها (واچ‌لیست و تاریخچه کش)", fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}
