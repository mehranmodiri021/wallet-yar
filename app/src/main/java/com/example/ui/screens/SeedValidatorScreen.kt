package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.TapsellBannerAd
import com.example.data.bip39.Bip39WordList
import com.example.ui.WalletRecoveryViewModel
import com.example.ui.theme.*

@Composable
fun SeedValidatorScreen(viewModel: WalletRecoveryViewModel) {
    val wordCount = viewModel.validatorWordCountMode.intValue
    val words = viewModel.validatorWords
    val result = viewModel.validatorResult.value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("seed_validator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tapsell Banner
        item {
            TapsellBannerAd()
        }

        // Prominent Security Warning Banner (Non-negotiable)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2818)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GreenSuccess.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GreenSuccess.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = GreenSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🚨 تضمین امنیت: کلمات شما هرگز از گوشی خارج نمی‌شوند",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenSuccess
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "پردازش اعتبارسنجی دیکشنری و چک‌سام SHA-256 صرفاً در حافظه موقت (RAM) پردازش شده و در هیچ دیتابیس یا سروری ذخیره نمی‌شود.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Mode Selector: 12 or 24 words + Clear
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = (wordCount == 12),
                            onClick = { viewModel.setValidatorWordCount(12) },
                            label = { Text("۱۲ کلمه", fontWeight = FontWeight.Bold) }
                        )
                        FilterChip(
                            selected = (wordCount == 24),
                            onClick = { viewModel.setValidatorWordCount(24) },
                            label = { Text("۲۴ کلمه", fontWeight = FontWeight.Bold) }
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.clearValidatorWords() },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedAlert)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("پاک‌سازی", fontSize = 11.sp)
                    }
                }
            }
        }

        // Word Grid (2 columns)
        item {
            val rows = wordCount / 2
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in 0 until rows) {
                    val idx1 = row * 2
                    val idx2 = row * 2 + 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ValidatorWordBox(
                            index = idx1,
                            word = words[idx1],
                            modifier = Modifier.weight(1f),
                            onValueChange = { viewModel.setValidatorWord(idx1, it) }
                        )
                        ValidatorWordBox(
                            index = idx2,
                            word = words[idx2],
                            modifier = Modifier.weight(1f),
                            onValueChange = { viewModel.setValidatorWord(idx2, it) }
                        )
                    }
                }
            }
        }

        // Validate Button
        item {
            Button(
                onClick = { viewModel.runValidatorCheck() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("validate_seed_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color(0xFF003038)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "اعتبارسنجی دیکشنری و چک‌سام عبارت ($wordCount کلمه)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Validation Result
        if (result != null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.isValid) Color(0xFF0D3320) else Color(0xFF381517)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (result.isValid) GreenSuccess else RedAlert,
                            RoundedCornerShape(14.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (result.isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (result.isValid) GreenSuccess else RedAlert,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (result.isValid) "عبارت بازیابی کاملاً معتبر است" else "خطا در عبارت بازیابی",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        if (result.errorMessage != null) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = result.errorMessage,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                        }

                        if (result.invalidWordIndices.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "کلمات نامعتبر شناسایی شده:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedAlert
                            )
                            result.invalidWordIndices.forEach { (idx, w) ->
                                Text(
                                    text = "• کلمه شماره ${idx + 1}: «$w» در فهرست استاندارد BIP-39 وجود ندارد.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
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

@Composable
private fun ValidatorWordBox(
    index: Int,
    word: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    val clean = word.trim().lowercase()
    val isValid = clean.isEmpty() || Bip39WordList.isValidWord(clean)

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!isValid) Color(0xFF331417) else CryptoSurfaceDark
        ),
        modifier = modifier.border(
            1.dp,
            when {
                !isValid -> RedAlert
                clean.isNotEmpty() -> GreenSuccess.copy(alpha = 0.5f)
                else -> CryptoBorderDark
            },
            RoundedCornerShape(10.dp)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (!isValid) RedAlert else Color(0xFF263352),
                shape = CircleShape,
                modifier = Modifier.size(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${index + 1}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isValid) Color.Black else Color.White
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            OutlinedTextField(
                value = word,
                onValueChange = onValueChange,
                singleLine = true,
                placeholder = { Text("کلمه ${index + 1}", fontSize = 11.sp, color = TextTertiary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
            )
        }
    }
}
