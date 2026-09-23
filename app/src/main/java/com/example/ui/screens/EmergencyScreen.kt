package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

@Composable
fun EmergencyScreen() {
    val context = LocalContext.current
    var compromisedAddress by remember { mutableStateOf("") }
    var theftTxHash by remember { mutableStateOf("") }
    var lostAmount by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("emergency_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Red Emergency Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF381316)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, RedAlert, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(RedAlert.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, RedAlert, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = RedAlert, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🚨 پروتکل اضطراری: نشت عبارات بازیابی (Seed Leaked)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "اقدامات فوری در ثانیه‌های اول پیش از تخلیه کامل دارایی",
                                fontSize = 11.sp,
                                color = RedAlert
                            )
                        }
                    }
                }
            }
        }

        // Action Checklist
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "📋 چک‌لیست اقدامات فوری:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    ChecklistItem(
                        number = "۱",
                        title = "یک کیف پول کاملاً جدید با Seed جدید بسازید",
                        description = "روی یک دستگاه امن و مجزا یک ولت جدید بسازید و آدرس آن را کپی کنید."
                    )

                    ChecklistItem(
                        number = "۲",
                        title = "انتقال دارایی‌های اصلی با کارمزد سریع (High Gas)",
                        description = "اگر هنوز دارایی در ولت باقی مانده، با اولویت کارمزد بالا آن را به آدرس جدید بفرستید."
                    )

                    ChecklistItem(
                        number = "۳",
                        title = "لغو دسترسی‌های مشکوک قراردادها (Revoke)",
                        description = "از سرویس Revoke.cash برای قطع دسترسی‌های توکن‌های نامحدود استفاده کنید."
                    )

                    ChecklistItem(
                        number = "۴",
                        title = "مراقب ربات‌های تخلیه‌کننده خودکار (Sweeper Bots) باشید",
                        description = "اگر هکر اسکریپت خودکار فعال کرده باشد، هر ارزی برای کارمزد واریز کنید بلافاصله غارت می‌شود. در این حالت از ابزارهای Flashbots استفاده نمایید."
                    )
                }
            }
        }

        // Cyber Police Link
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF142036)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanPrimary, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalPolice, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "سامانه رسمی پلیس فتا جمهوری اسلامی ایران",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "در صورت وقوع هرگونه کلاهبرداری، سرقت رمزارز یا سایت‌های فیشینگ، فوراً مراتب را در مرکز فوریت‌های سایبری پلیس فتا ثبت کنید.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://csirc.cyberpolice.ir/"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF003038)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("ورود به سامانه ثبت شکایت پلیس فتا (cyberpolice.ir)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Pre-made Legal Report Generator
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "📝 فرم گزارش آماده برای ارائه به مراجع قضایی:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )

                    OutlinedTextField(
                        value = compromisedAddress,
                        onValueChange = { compromisedAddress = it },
                        label = { Text("آدرس کیف پول شما (قربانی)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = theftTxHash,
                        onValueChange = { theftTxHash = it },
                        label = { Text("هش تراکنش سرقت (Transaction Hash)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = lostAmount,
                        onValueChange = { lostAmount = it },
                        label = { Text("میزان دارایی به سرقت رفته و نام ارز") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val template = """
بسمه تعالی
موضوع: گزارش سرقت دارایی دیجیتال و فیشینگ
به: مراجع محترم قضایی / پلیس محترم فتا

اینجانب بدین‌وسیله گزارش می‌دهم که دارایی دیجیتال اینجانب به آدرس عمومی:
$compromisedAddress
مورد سرقت و سوءاستفاده افراد ناشناس قرار گرفته است.

مشخصات تراکنش مشکوک/سرقت:
- هش تراکنش (TxHash): $theftTxHash
- مقدار تقریبی دارایی به سرقت رفته: $lostAmount
- تاریخ رخداد: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}

خواهشمند است دستور پیگیری و ردیابی فنی تراکنش در صرافی‌های داخلی و بین‌المللی را مبذول فرمایید.
                            """.trimIndent()

                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Legal Report", template))
                            Toast.makeText(context, "متن شکواییه کپی شد", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("کپی متن رسمی شکواییه جهت پیگیری قضایی", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ChecklistItem(number: String, title: String, description: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Surface(
            color = Color(0xFF263352),
            shape = CircleShape,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(number, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(2.dp))
            Text(description, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
        }
    }
}
