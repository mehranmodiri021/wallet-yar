package com.example.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.TapsellAdManager
import com.example.ui.theme.*

/**
 * Cafe Bazaar In-App Purchase & VIP Subscription Manager
 * پشتیبانی از پرداخت درون‌برنامه‌ای کافه‌بازار (Poolkey & In-App Billing)
 */
object CafeBazaarBillingManager {
    const val BAZAAR_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..." // Cafe Bazaar RSA Key
    const val SKU_VIP_MONTHLY = "vip_sub_monthly"
    const val SKU_VIP_LIFETIME = "vip_sub_lifetime"

    var isSubscribed = mutableStateOf(false)
}

@Composable
fun BazaarVipSubscriptionDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf(1) } // 1: Monthly, 2: Lifetime
    var isProcessing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Brush.linearGradient(listOf(GoldAccent, Color(0xFFFF8F00))),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ارتقا به اشتراک ویژه (VIP کافه‌بازار)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "دسترسی نامحدود و بدون تبلیغات",
                        fontSize = 11.sp,
                        color = GoldAccent
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "با تهیه اشتراک ویژه VIP از درگاه پرداخت امن کافه‌بازار، به تمامی امکانات زیر دسترسی پیدا کنید:",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )

                // Feature items
                VipFeatureItem(title = "حذف کامل تمام تبلیغات تپسل و بنرها")
                VipFeatureItem(title = "جستجوی نامحدود و همزمان تا ۳ کلمه مفقود BIP-39")
                VipFeatureItem(title = "استعلام خودکار موجودی بلاک‌چین با API اختصاصی Blockchair")
                VipFeatureItem(title = "پشتیبانی اولویت‌دار و فارنزیک اختصاصی")

                Spacer(Modifier.height(4.dp))

                // Plan selection
                PlanOptionCard(
                    title = "اشتراک ماهانه VIP",
                    price = "۴۹,۰۰۰ تومان / ماه",
                    selected = (selectedPlan == 1),
                    onClick = { selectedPlan = 1 }
                )

                PlanOptionCard(
                    title = "اشتراک مادام‌العمر طلایی (پیشنهاد ویژه)",
                    price = "۱۴۹,۰۰۰ تومان (یکبار برای همیشه)",
                    selected = (selectedPlan == 2),
                    onClick = { selectedPlan = 2 }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "پرداخت امن تحت نظارت رسمی کافه‌بازار",
                        fontSize = 10.sp,
                        color = TextTertiary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isProcessing = true
                    // Simulate Cafe Bazaar billing flow
                    CafeBazaarBillingManager.isSubscribed.value = true
                    TapsellAdManager.isVipSubscriber.value = true
                    isProcessing = false
                    onSuccess()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("خرید از کافه‌بازار", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("بعداً")
            }
        },
        containerColor = CardBackground,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun VipFeatureItem(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Check, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(text = title, fontSize = 11.sp, color = TextPrimary)
    }
}

@Composable
private fun PlanOptionCard(
    title: String,
    price: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF2A2410) else CryptoSurfaceDark
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (selected) GoldAccent else CryptoBorderDark,
                RoundedCornerShape(10.dp)
            ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selected) GoldAccent else Color.White)
                Text(text = price, fontSize = 11.sp, color = TextSecondary)
            }
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
            )
        }
    }
}
