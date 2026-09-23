package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.TapsellBannerAd
import com.example.data.network.Chain
import com.example.ui.WalletRecoveryViewModel
import com.example.ui.theme.*

@Composable
fun BalanceScannerScreen(viewModel: WalletRecoveryViewModel) {
    val address = viewModel.scannerAddress.value
    val chain = viewModel.selectedChain.value
    val detectedFormat = viewModel.detectedFormat.value
    val isScanning = viewModel.isScanning.value
    val result = viewModel.scanResult.value
    val isFromCache = viewModel.isFromCache.value
    val context = LocalContext.current

    var customLabelInput by remember { mutableStateOf("") }
    var showSavedMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("balance_scanner_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tapsell Banner
        item {
            TapsellBannerAd()
        }

        // Header info
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
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "استعلام موجودی عمومی و تشخیص فرمت",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "استعلام زنده از Blockchair API + کش ۲۴ ساعته در دیتابیس محلی",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "فقط آدرس‌های عمومی (Public Address) را وارد کنید. هرگز کلید خصوصی یا عبارت ۱۲ کلمه‌ای را در این کادر قرار ندهید.",
                        fontSize = 11.sp,
                        color = GoldAccent
                    )
                }
            }
        }

        // Input Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { viewModel.onScannerAddressChange(it) },
                        label = { Text("آدرس عمومی کیف پول") },
                        placeholder = { Text("مثال: 1A1zP1eP... یا 0x71C... یا bc1q...") },
                        singleLine = true,
                        trailingIcon = {
                            if (address.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onScannerAddressChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "پاک کردن")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Detected Format Badge
                    if (detectedFormat.formatNameFa != "فرمت ناشناخته") {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF142436)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "تشخیص هوشمند: ${detectedFormat.formatNameFa}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanPrimary
                                    )
                                    Text(
                                        text = detectedFormat.descriptionFa,
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Chain selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Chain.entries.take(4).forEach { c ->
                            FilterChip(
                                selected = (chain == c),
                                onClick = { viewModel.selectedChain.value = c },
                                label = { Text(c.symbol, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    // Scan Action Button
                    Button(
                        onClick = { viewModel.checkAddressBalance(address, chain) },
                        enabled = !isScanning && address.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF003038)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("scan_balance_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF003038))
                            Spacer(Modifier.width(8.dp))
                            Text("در حال استعلام از بلاک‌چین...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("استعلام موجودی و تراکنش‌ها", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Preset quick links
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.loadPresetScannerAddress("btc") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("ساتوشی (BTC)", fontSize = 10.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.loadPresetScannerAddress("eth") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("ویتالیک (ETH)", fontSize = 10.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.loadPresetScannerAddress("tron") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("تتر (TRX)", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Result Card
        if (result != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GreenSuccess.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "نتایج استعلام ${result.detectedChain}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // 24h Cache Badge
                            if (isFromCache) {
                                Surface(
                                    color = GoldAccent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                                ) {
                                    Text(
                                        text = "کش ۲۴ ساعته",
                                        color = GoldAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    color = GreenSuccess.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "زنده از بلاک‌چین",
                                        color = GreenSuccess,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = CryptoBorderDark)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("موجودی:", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "${result.balance} ${result.balanceSymbol}",
                                color = GoldAccent,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("تعداد تراکنش‌ها:", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "${result.txCount} تراکنش",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = result.details,
                            color = TextTertiary,
                            fontSize = 10.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.explorerUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("مشاهده در Blockchair", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.saveCurrentScanToWatchlist(customLabelInput)
                                    showSavedMessage = true
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF233B6E))
                            ) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(if (showSavedMessage) "ذخیره شد ✓" else "افزودن به واچ‌لیست", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(30.dp))
        }
    }
}
