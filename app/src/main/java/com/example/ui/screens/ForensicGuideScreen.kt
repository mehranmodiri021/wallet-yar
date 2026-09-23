package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ForensicTopic(
    val title: String,
    val category: String,
    val summary: String,
    val steps: List<String>,
    val tips: String
)

@Composable
fun ForensicGuideScreen() {
    val topics = remember {
        listOf(
            ForensicTopic(
                title = "بازیابی فایل wallet.dat در ویندوز و سیستم‌های قدیمی",
                category = "بیت‌کوین کور و لایت‌کوین",
                summary = "اگر در سال‌های ۲۰۱۰ تا ۲۰۱۷ کلاینت رسمی بیت‌کوین (Bitcoin-Qt یا Bitcoin Core) را نصب کرده بودید، کلیدهای شما در این فایل ذخیره شده است.",
                steps = listOf(
                    "کلید میانبر Win + R را در ویندوز فشار دهید و عبارت %appdata%\\Bitcoin را تایپ و اینتر بزنید.",
                    "به دنبال فایلی به نام wallet.dat بگردید. اگر هارد فرمت شده، از نرم‌افزارهای ریکاوری فایل (مانند PhotoRec یا Recuva) با فیلتر پسوند .dat استفاده کنید.",
                    "فایل wallet.dat را در نسخه جدید Bitcoin Core یا نرم‌افزار Electrum (بخش Import) قرار دهید.",
                    "اگر روی فایل رمز عبور گذاشته بودید و آن را فراموش کرده‌اید، ابزار رسمی btcrecover می‌تواند بر اساس حدس‌های شما رمز را تست کند."
                ),
                tips = "نکته مهم: هرگز فایل اصلی wallet.dat را دستکاری نکنید؛ ابتدا یک کپی بکاپ (Copy & Paste) روی فلش درایو تهیه فرمایید."
            ),
            ForensicTopic(
                title = "استخراج اطلاعات کیف‌پول متامسک از حافظه مرورگر کروم",
                category = "MetaMask Vault Recovery",
                summary = "اگر مرورگر یا ویندوز خراب شده اما پوشه User Data کروم باقی مانده، می‌توانید صندوق رمزنگاری شده (Vault) متامسک را بازیابی کنید.",
                steps = listOf(
                    "پوشه داده‌های کروم را پیدا کنید: %LocalAppData%\\Google\\Chrome\\User Data\\Default\\Local Extension Settings",
                    "به دنبال پوشه اکستنشن متامسک بگردید (کد: nkbihfbeogaeaoehlefnkodbefgpgknn).",
                    "فایل‌های با پسوند .ldb و .log داخل پوشه IndexedDB یا Local Storage را با ابزار متن‌باز MetaMask Vault Decryptor باز کنید.",
                    "با وارد کردن رمز ورود قبلی مرورگر، عبارت ۱۲ کلمه‌ای بازیابی می‌گردد."
                ),
                tips = "این روش نجات‌بخش کاربرانی است که رایانه‌شان بالا نمی‌آید اما به فایل‌های هارددیسک دسترسی دارند."
            ),
            ForensicTopic(
                title = "بازیابی کیف‌پول‌های کاغذی (Paper Wallets)",
                category = "کلیدهای کاغذی و WIF",
                summary = "کیف‌پول‌هایی که روی کاغذ چاپ شده‌اند معمولاً شامل یک کلید خصوصی به فرمت WIF یا کد QR هستند.",
                steps = listOf(
                    "اگر کلید خصوصی با عدد ۵، K یا L شروع می‌شود (طول ۵۱ یا ۵۲ کاراکتر)، یک کلید WIF خام است.",
                    "اگر با 6P شروع می‌شود، یک کیف‌پول با استاندارد BIP-38 است که نیازمند رمز عبوری است که زمان ساخت وارد کرده بودید.",
                    "نرم‌افزار معتبر Electrum را روی کامپیوتر یا BlueWallet را روی گوشی نصب کنید.",
                    "گزینه Sweep Private Key را انتخاب کنید تا موجودی بلافاصله به یک کیف‌پول جدید امن منتقل شود."
                ),
                tips = "هشدار: هرگز کلید کاغذی را با دوربین برنامه‌های ناشناس اسکن نکنید."
            ),
            ForensicTopic(
                title = "جستجوی ایمیل برای کشف حساب‌های صرافی‌های فراموش شده",
                category = "صرافی‌ها و سرویس‌های آنلاین",
                summary = "بسیاری از افراد بیت‌کوین‌های قدیمی خود را در صرافی‌هایی که سال‌ها پیش ثبت‌نام کرده بودند فراموش کرده‌اند.",
                steps = listOf(
                    "ایمیل‌های قدیمی خود (Gmail، Yahoo، Outlook) را باز کنید.",
                    "این کلیدواژه‌ها را در کادر جستجو سرچ کنید: 'Deposit confirmed'، 'Withdrawal'، 'Verification'، 'BTC'، 'Crypto'.",
                    "نام صرافی‌های قدیمی را جستجو کنید: Mt.Gox، BTC-e، Poloniex، Bittrex، Bitfinex، Cryptopia، Kraken.",
                    "در صورت پیدا کردن ایمیل تایید حساب، از گزینه 'Forgot Password' یا ارسال تیکت پشتیبانی به همراه احراز هویت مدارک هویتی خود اقدام نمایید."
                ),
                tips = "حتی صرافی‌های ورشکسته مانند Mt.Gox یا Bittrex پروسه‌های بازپرداخت به طلبکاران دارند که با ایمیل رسمی قابل پیگیری است."
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("forensic_guide_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "راهنمای گام‌به‌گام بازیابی فنی (Forensics)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "دستورالعمل استخراج دارایی از هارد، مرورگر و کاغذ",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "روش‌های عملی و تایید شده جهانی برای نجات دارایی‌هایی که رمز عبور یا فایل‌هایشان در رایانه‌های قدیمی جا مانده است.",
                        fontSize = 12.sp,
                        color = TextTertiary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        items(topics) { topic ->
            ForensicTopicCard(topic = topic)
        }

        item {
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun ForensicTopicCard(topic: ForensicTopic) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .border(1.dp, CryptoBorderDark, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = topic.category,
                        fontSize = 10.sp,
                        color = CyanPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }

            Text(
                text = topic.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = topic.summary,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = CryptoBorderDark)

                    Text(
                        text = "مراحل انجام کار:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    topic.steps.forEachIndexed { idx, step ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "${idx + 1}. ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                            Text(
                                text = step,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 17.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Surface(
                        color = CryptoSurfaceDark,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            text = topic.tips,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
