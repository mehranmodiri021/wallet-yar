package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class WalletGuide(
    val name: String,
    val iconName: String,
    val type: String,
    val officialUrl: String,
    val steps: List<String>,
    val tips: String
)

@Composable
fun RecoveryGuideScreen() {
    val context = LocalContext.current
    var selectedWalletIndex by remember { mutableIntStateOf(0) }

    val guides = remember {
        listOf(
            WalletGuide(
                name = "MetaMask (متامسک)",
                iconName = "🦊",
                type = "EVM / Ethereum / Polygon",
                officialUrl = "https://metamask.io/download/",
                steps = listOf(
                    "اپلیکیشن رسمی متامسک را فقط از گوگل‌پلی یا وبسایت رسمی metamask.io دریافت کنید.",
                    "روی گزینه «Import using Secret Recovery Phrase» کلیک کنید.",
                    "۱۲ کلمه بازیابی را با ترتیب دقیق، با حروف کوچک و فاصله بین هر کلمه تایپ کنید.",
                    "یک رمز عبور جدید برای دستگاه فعلی تعیین کرده و ورود را تأیید کنید.",
                    "در صورت عدم نمایش موجودی توکن‌ها، از بخش «Import Tokens» آدرس قرارداد را اضافه کنید یا شبکه مربوطه (مانند BSC یا Polygon) را فعال نمایید."
                ),
                tips = "متامسک هیچ‌گاه به صورت پیامکی یا ایمیلی از شما کلمات بازیابی را نخواهد خواست."
            ),
            WalletGuide(
                name = "Trust Wallet (تراست ولت)",
                iconName = "🛡️",
                type = "Multi-Chain (BTC, ETH, BNB, TRX)",
                officialUrl = "https://trustwallet.com/download",
                steps = listOf(
                    "اپلیکیشن تراست ولت را باز کرده و گزینه «I already have a wallet» را انتخاب کنید.",
                    "گزینه «Multi-Coin Wallet» را جهت بازیابی همزمان تمامی شبکه‌ها انتخاب نمایید.",
                    "عبارت بازیابی ۱۲ کلمه‌ای خود را با فاصله‌گذاری دقیق وارد کنید.",
                    "پس از ورود، در صفحه اصلی برای اضافه کردن رمزارزها آیکون فیلتر بالا را لمس کرده و رمزارز مد نظر را فعال کنید."
                ),
                tips = "نسخه‌های وب جعلی زیادی برای تراست ولت وجود دارد؛ هیچ‌گاه کلمات را در سایت وارد نکنید."
            ),
            WalletGuide(
                name = "Ledger (لجر سخت‌افزاری)",
                iconName = "🔒",
                type = "Hardware Cold Storage (Nano S / X / Stax)",
                officialUrl = "https://www.ledger.com/ledger-live",
                steps = listOf(
                    "دستگاه لجر را به کامپیوتر متصل کرده و هر دو دکمه را فشار دهید تا وارد منو شوید.",
                    "گزینه «Restore from recovery phrase» را روی خود دستگاه انتخاب کنید.",
                    "تعداد کلمات (معمولاً ۲۴ کلمه در لجر) را تعیین کنید.",
                    "هر کلمه را با دکمه‌های روی لجر انتخاب کنید (کلمات در کامپیوتر تایپ نمی‌شوند).",
                    "نرم‌افزار Ledger Live را باز کرده و حساب‌های مربوطه را همگام‌سازی کنید."
                ),
                tips = "کلمات لجر را هرگز درون هیچ کیبورد کامپیوتر یا نرم‌افزاری تایپ نکنید."
            ),
            WalletGuide(
                name = "Phantom (فانتوم)",
                iconName = "👻",
                type = "Solana & Multi-Chain",
                officialUrl = "https://phantom.app/download",
                steps = listOf(
                    "کیف پول فانتوم را دانلود و نصب کنید.",
                    "روی دکمه «I already have a wallet» کلیک کنید.",
                    "عبارت ۱۲ کلمه‌ای را وارد کرده و یک پسورد محلی انتخاب کنید.",
                    "شبکه‌های سولانا، اتریوم و بیت‌کوین به صورت خودکار شناسایی خواهند شد."
                ),
                tips = "در شبکه سولانا، توکن‌های مشکوک با جایزه کلاهبرداری ایردراپ می‌شوند، روی لینک آن‌ها کلیک نکنید."
            ),
            WalletGuide(
                name = "Exodus (اکسودوس)",
                iconName = "🚀",
                type = "Desktop & Mobile Multi-Asset",
                officialUrl = "https://www.exodus.com/download/",
                steps = listOf(
                    "در هنگام شروع، گزینه «Restore from Backup» را بزنید.",
                    "عبارت ۱۲ کلمه‌ای استاندارد BIP-39 خود را وارد کنید.",
                    "برنامه از شما می‌خواهد ری‌استارت شود و بلاک‌چین‌ها را اسکن نماید.",
                    "اگر موجودی صفر نمایش داده شد، از منوی Settings گزینه Refresh Blockchain را بزنید."
                ),
                tips = "اکسودوس امکان پشتیبان‌گیری از کلید خصوصی هر آدرس به صورت تفکیک شده را نیز فراهم می‌کند."
            )
        )
    }

    val currentGuide = guides[selectedWalletIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("recovery_guide_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(CyanPrimary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "راهنمای گام‌به‌گام بازیابی در کیف پول‌ها",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "آموزش تصویری و تخصصی وارد کردن عبارات بازیابی (Seed Import)",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Wallet Tabs Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                guides.forEachIndexed { index, guide ->
                    FilterChip(
                        selected = (selectedWalletIndex == index),
                        onClick = { selectedWalletIndex = index },
                        label = { Text("${guide.iconName} ${guide.name.split(" ")[0]}", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Detailed Guide Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(currentGuide.iconName, fontSize = 28.sp)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = currentGuide.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = currentGuide.type,
                                    fontSize = 11.sp,
                                    color = CyanPrimary
                                )
                            }
                        }

                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentGuide.officialUrl))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "وبسایت رسمی", tint = CyanPrimary)
                        }
                    }

                    Divider(color = CryptoBorderDark)

                    Text(
                        text = "مراحل گام‌به‌گام بازیابی:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    currentGuide.steps.forEachIndexed { stepIdx, stepText ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                            Surface(
                                color = Color(0xFF1E2F4A),
                                shape = CircleShape,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("${stepIdx + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = stepText,
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF261D15)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "نکته امنیتی: ${currentGuide.tips}",
                                fontSize = 11.sp,
                                color = GoldAccent,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentGuide.officialUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF003038)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("دانلود نسخه رسمی ${currentGuide.name.split(" ")[0]}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(30.dp))
        }
    }
}
