package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AboutScreen() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("about_screen")
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(10.dp))

        Surface(
            color = CyanPrimary.copy(alpha = 0.15f),
            shape = CircleShape,
            modifier = Modifier
                .size(72.dp)
                .border(2.dp, CyanPrimary, CircleShape)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.VpnKey,
                    contentDescription = "آیکون اصلی کیف پول یار",
                    tint = CyanPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Text(
            text = "کیف پول یار (Wallet Yar)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Surface(
            color = CryptoSurfaceDark,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "نسخه ۱.۰.۰ (Release Version 1.0.0)",
                fontSize = 12.sp,
                color = CyanPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        // Developer Info Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "اطلاعات توسعه و پشتیبانی:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("سازنده و صاحب اثر:", fontSize = 12.sp, color = TextSecondary)
                    Text("سیدحمید موسوی زاده", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ایمیل پشتیبانی رسمی:", fontSize = 12.sp, color = TextSecondary)
                    Text("support@walletyar.ir", fontSize = 12.sp, color = CyanPrimary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("حریم خصوصی و داده‌ها:", fontSize = 12.sp, color = TextSecondary)
                    Text("۱۰۰٪ آفلاین (بدون ارسال داده)", fontSize = 12.sp, color = GreenSuccess)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("پایگاه داده BIP-39:", fontSize = 12.sp, color = TextSecondary)
                    Text("Room SQLite (۲۰۴۸ کلمه استاندارد)", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        // Full Legal Disclaimer
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1517)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RedAlert.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = RedAlert, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "سلب مسئولیت قانونی (Disclaimer):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedAlert
                    )
                }

                Text(
                    text = "«این برنامه ابزاری آموزشی برای کمک به افرادی است که در فرآیند بازیابی کیف پول خود دچار مشکل شده‌اند. این برنامه به هیچ عنوان قادر به دسترسی، حدس زدن یا بازیابی کلیدهای خصوصی یا عبارات بازیابی (Seed Phrase) بدون اطلاع و همکاری صاحب اصلی نیست. هرگونه استفاده غیرمجاز، هک، یا سوءاستفاده از این ابزار پیگرد قانونی دارد و مسئولیت آن بر عهده کاربر است. این برنامه هیچگونه دارایی دیجیتال را در سرور خود نگهداری نمی‌کند و تمام پردازش‌های حساس فقط روی دستگاه کاربر انجام می‌شود.»",
                    fontSize = 11.sp,
                    color = Color.White,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Justify
                )
            }
        }

        // Privacy Policy & Support Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@walletyar.ir")
                        putExtra(Intent.EXTRA_SUBJECT, "پشتیبانی کیف پول یار")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("تماس با پشتیبانی", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://walletyar.ir/privacy.html"))
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Policy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("سیاست حفظ حریم خصوصی", fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}
