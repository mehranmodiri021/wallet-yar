package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
import com.example.ads.TapsellBannerAd
import com.example.data.bip39.Bip39WordList
import com.example.data.bip39.MultiRecoveryCandidate
import com.example.ui.WalletRecoveryViewModel
import com.example.ui.theme.*

@Composable
fun MissingWordsScreen(viewModel: WalletRecoveryViewModel) {
    val words = viewModel.missingWordsInput
    val missingIndices = viewModel.missingIndices
    val partialValidation = viewModel.partialValidationState.value
    val isSearching = viewModel.isSearchingMissing.value
    val testedCount = viewModel.searchTestedCount.value
    val candidates = viewModel.searchFoundCandidates.value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("missing_words_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tapsell Banner Ad
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
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "جستجوی کلمات گمشده (۱ تا ۳ کلمه)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "بررسی هوشمند با چک‌سام SHA-256 (کاملاً آفلاین در RAM)",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "اگر کلمه‌ای مخدوش یا ناخوانا شده است، آیکون علامت‌سوال را روی آن کلمه بزنید تا مفقود علامت بخورد. الگوریتم با بررسی فرمول چک‌سام کلمات معتبر را استخراج می‌کند.",
                        fontSize = 12.sp,
                        color = TextTertiary,
                        lineHeight = 17.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.loadMissingPreset(1) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text("۱ مفقود", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.loadMissingPreset(2) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text("۲ مفقود", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.loadMissingPreset(3) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text("۳ مفقود", fontSize = 11.sp)
                        }
                        IconButton(onClick = { viewModel.clearMissingWords() }) {
                            Icon(Icons.Default.Clear, contentDescription = "پاک کردن", tint = TextSecondary)
                        }
                    }
                }
            }
        }

        // Partial validation status
        if (partialValidation != null) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (partialValidation.isValid) Color(0xFF143022) else Color(0xFF381B1D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (partialValidation.isValid) Icons.Default.Info else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (partialValidation.isValid) GreenSuccess else RedAlert,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = partialValidation.message,
                            fontSize = 12.sp,
                            color = Color.White,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // 12 Words Input Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in 0 until 6) {
                    val idx1 = row * 2
                    val idx2 = row * 2 + 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MissingWordCard(
                            index = idx1,
                            word = words[idx1],
                            isMissing = missingIndices.contains(idx1),
                            modifier = Modifier.weight(1f),
                            onWordChange = { viewModel.setMissingWordInput(idx1, it) },
                            onToggleMissing = { viewModel.toggleMissingWordIndex(idx1) }
                        )
                        MissingWordCard(
                            index = idx2,
                            word = words[idx2],
                            isMissing = missingIndices.contains(idx2),
                            modifier = Modifier.weight(1f),
                            onWordChange = { viewModel.setMissingWordInput(idx2, it) },
                            onToggleMissing = { viewModel.toggleMissingWordIndex(idx2) }
                        )
                    }
                }
            }
        }

        // Prefix hints & Search Action
        if (missingIndices.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "تنظیمات کشف ${missingIndices.size} کلمه مفقود:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )

                        Text(
                            text = "پیشوند (چند حرف اول در صورت یادآوری برای افزایش سرعت):",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        missingIndices.forEach { idx ->
                            OutlinedTextField(
                                value = viewModel.prefixHints[idx],
                                onValueChange = { viewModel.setMissingPrefixHint(idx, it) },
                                label = { Text("پیشوند کلمه شماره ${idx + 1}") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.startMissingWordSearch() },
                                enabled = !isSearching,
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color.Black),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("start_search_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
                                    Spacer(Modifier.width(8.dp))
                                    Text("تست چک‌سام: %,d".format(testedCount), fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("جستجوی کلمه گمشده", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            if (isSearching) {
                                OutlinedButton(
                                    onClick = { viewModel.cancelMissingWordSearch() },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedAlert),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("لغو", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Candidates Found
        if (candidates.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ترکیب‌های احتمالی معتبر (${candidates.size} نتیجه پیدا شد):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenSuccess
                    )

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            candidates.take(25).forEach { candidate ->
                                CandidateRow(
                                    candidate = candidate,
                                    onApply = { viewModel.applyMissingCandidate(candidate) }
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
private fun MissingWordCard(
    index: Int,
    word: String,
    isMissing: Boolean,
    modifier: Modifier = Modifier,
    onWordChange: (String) -> Unit,
    onToggleMissing: () -> Unit
) {
    val clean = word.trim().lowercase()
    val hasTypo = clean.isNotEmpty() && !Bip39WordList.isValidWord(clean)

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isMissing -> GoldAccent.copy(alpha = 0.15f)
                hasTypo -> RedAlert.copy(alpha = 0.15f)
                else -> CryptoSurfaceDark
            }
        ),
        modifier = modifier.border(
            1.dp,
            when {
                isMissing -> GoldAccent
                hasTypo -> RedAlert
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
                color = when {
                    isMissing -> GoldAccent
                    hasTypo -> RedAlert
                    else -> Color(0xFF263352)
                },
                shape = CircleShape,
                modifier = Modifier.size(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${index + 1}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMissing || hasTypo) Color.Black else Color.White
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            if (isMissing) {
                Text(
                    text = "؟ (مفقود)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp)
                )
            } else {
                OutlinedTextField(
                    value = word,
                    onValueChange = onWordChange,
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
                        .height(48.dp)
                )
            }

            IconButton(
                onClick = onToggleMissing,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isMissing) Icons.Default.Edit else Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = "علامت‌گذاری مفقود",
                    tint = if (isMissing) GoldAccent else TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CandidateRow(
    candidate: MultiRecoveryCandidate,
    onApply: () -> Unit
) {
    val description = candidate.recoveredWords.entries
        .sortedBy { it.key }
        .joinToString("  |  ") { "کلمه ${it.key + 1}: ${it.value}" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onApply)
            .background(Color(0xFF19253C), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Key, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        FilledTonalButton(
            onClick = onApply,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.height(30.dp)
        ) {
            Text("جایگذاری", fontSize = 11.sp)
        }
    }
}
