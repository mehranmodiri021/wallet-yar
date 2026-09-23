package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ScamAddressInfo(
    val address: String,
    val chain: String,
    val scamType: String,
    val reportedVictims: Int,
    val description: String
)

@Composable
fun ScamAlertScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var isCheckingScam by remember { mutableStateOf(false) }
    var searchResultAlert by remember { mutableStateOf<String?>(null) }

    val knownScamAddresses = remember {
        listOf(
            ScamAddressInfo(
                address = "1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF",
                chain = "Bitcoin",
                scamType = "Mt. Gox Hacked Funds",
                reportedVictims = 1000,
                description = "آدرس مسروقه معروف صرافی Mt. Gox و تحت نظارت و تحریم صرافی‌های جهانی."
            ),
            ScamAddressInfo(
                address = "0x098B716B8Aaf21512996dC57EB0615e2383E2f96",
                chain = "Ethereum",
                scamType = "Fake Recovery Bot Phishing",
                reportedVictims = 24,
                description = "ربات جعلی تلگرامی مدعی بازیابی ولت که کلید خصوصی قربانیان را غارت می‌کرد."
            ),
            ScamAddressInfo(
                address = "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045",
                chain = "Ethereum",
                scamType = "Phishing Impersonation",
                reportedVictims = 0,
                description = "آدرس‌های جعلی شبیه به آدرس‌های افراد مشهور جهت فریب در تراکنش‌های شتاب‌زده."
            ),
            ScamAddressInfo(
                address = "THPzhH4YF8w1aK1Z87n5hK4j5mQvK1t4w8",
                chain = "Tron",
                scamType = "Fake USDT Tether Multi-sig Trap",
                reportedVictims = 87,
                description = "آدرس‌های چندامضایی ترون که با انتشار Seed عمومی کاربران را ترغیب به واریز ترون می‌کردند."
            )
        )
    }

    val filteredList = knownScamAddresses.filter {
        it.address.contains(searchQuery, ignoreCase = true) ||
                it.scamType.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("scam_alert_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Warning Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF381518)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RedAlert, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(RedAlert.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = RedAlert, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "هشدار: کلاهبرداری ثانویه (Recovery Scams)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "مراقب افرادی که در اینستاگرام و تلگرام ادعای بازیابی ولت دارند باشید!",
                                fontSize = 11.sp,
                                color = RedAlert
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "هیچ فرد، شرکت، پلیس یا هکری نمی‌تواند بدون کلمات یا کلید خصوصی شما، به کیف پول گمشده‌تان دسترسی پیدا کند. هرکسی که وعده «هک و بازگرداندن دارایی مسروقه» در قبال بیعانه یا کارمزد اولیه می‌دهد، ۱۰۰٪ کلاهبردار است.",
                        fontSize = 11.sp,
                        color = Color.White,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Search Bar
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("بررسی آدرس مشکوک در لیست سیاه...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            isCheckingScam = true
                            searchResultAlert = if (filteredList.isNotEmpty()) {
                                "⚠️ هشدار: این آدرس در پایگاه داده آدرس‌های کلاهبرداری ثبت شده است!"
                            } else {
                                "✓ این آدرس در لیست سیاه محلی گزارش نشده است (با این حال همواره احتیاط کنید)."
                            }
                            isCheckingScam = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF003038)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Policy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("استعلام از پایگاه داده و هشدارهای امنیتی", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    if (searchResultAlert != null) {
                        Text(
                            text = searchResultAlert!!,
                            color = if (searchResultAlert!!.contains("هشدار")) RedAlert else GreenSuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // List
        items(filteredList) { item ->
            ScamAddressCard(item = item)
        }

        item {
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ScamAddressCard(item: ScamAddressInfo) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, RedAlert.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = RedAlert.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.scamType,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedAlert,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = item.chain,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = item.address,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.White
            )

            Text(
                text = item.description,
                fontSize = 11.sp,
                color = TextTertiary,
                lineHeight = 16.sp
            )
        }
    }
}
