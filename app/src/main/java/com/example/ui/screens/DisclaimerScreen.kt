package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * صفحه سلب مسئولیت و اصول بنیادین (نمایش اجباری در اولین اجرای برنامه)
 */
@Composable
fun DisclaimerScreen(onAccept: () -> Unit) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("disclaimer_screen"),
        color = CryptoNavyDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(RedAlert.copy(alpha = 0.2f), CircleShape)
                    .border(2.dp, RedAlert, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = RedAlert,
                    modifier = Modifier.size(38.dp)
                )
            }

            Text(
                text = "بیانیه رسمی سلب مسئولیت و تعهدنامه قانونی",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "اپلیکیشن «کیف پول یار» (Wallet Yar)",
                fontSize = 13.sp,
                color = CyanPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261316)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RedAlert.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "«این برنامه ابزاری آموزشی برای کمک به افرادی است که در فرآیند بازیابی کیف پول خود دچار مشکل شده‌اند. این برنامه به هیچ عنوان قادر به دسترسی، حدس زدن یا بازیابی کلیدهای خصوصی یا عبارات بازیابی (Seed Phrase) بدون اطلاع و همکاری صاحب اصلی نیست. هرگونه استفاده غیرمجاز، هک، یا سوءاستفاده از این ابزار پیگرد قانونی دارد و مسئولیت آن بر عهده کاربر است. این برنامه هیچگونه دارایی دیجیتال را در سرور خود نگهداری نمی‌کند و تمام پردازش‌های حساس فقط روی دستگاه کاربر انجام می‌شود.»",
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🎯 اصول بنیادین و غیرقابل تغییر این برنامه:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    RuleItem("۱. هیچ ادعای غیرواقعی «پیدا کردن ولت گمشده دیگران» وجود ندارد.")
                    RuleItem("۲. عبارات بازیابی (Seed) کاربر هرگز از دستگاه خارج نشده و به هیچ سروری ارسال نمی‌شود.")
                    RuleItem("۳. کلمات بازیابی در هیچ پایگاه داده، فایل ذخیره‌سازی یا لاگ ثبت نمی‌گردند.")
                    RuleItem("۴. اپلیکیشن کاملاً شفاف، اخلاقی و تابع قوانین جمهوری اسلامی ایران است.")
                    RuleItem("۵. هدف: آموزش علمی + ابزار بازیابی قانونی + جلوگیری از کلاهبرداری ثانویه.")
                }
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("accept_disclaimer_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color(0xFF003038)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Shield, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "می‌پذیرم و ادامه به برنامه",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RuleItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = GreenSuccess,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 18.sp
        )
    }
}
