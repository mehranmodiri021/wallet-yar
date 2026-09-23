package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.TapsellInterstitialAd
import com.example.billing.BazaarVipSubscriptionDialog
import com.example.billing.CafeBazaarBillingManager
import com.example.security.SecurityHelper
import com.example.ui.WalletRecoveryViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: WalletRecoveryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(
                    viewModel = viewModel,
                    onUpdateSecureFlag = { secure ->
                        SecurityHelper.setSecureFlag(this@MainActivity, secure)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: WalletRecoveryViewModel,
    onUpdateSecureFlag: (Boolean) -> Unit
) {
    val currentTab = viewModel.currentTab.intValue
    val isVip = CafeBazaarBillingManager.isSubscribed.value
    val isDisclaimerAccepted = viewModel.isDisclaimerAccepted.value

    var showVipDialog by remember { mutableStateOf(false) }
    var showInterstitialAd by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Enforce FLAG_SECURE on sensitive seed validator / missing words screens
    LaunchedEffect(currentTab) {
        val isSensitive = (currentTab == 1 || currentTab == 2)
        onUpdateSecureFlag(isSensitive)
    }

    // Interstitial ad triggered occasionally on navigation for non-VIP users
    TapsellInterstitialAd(
        show = showInterstitialAd,
        onDismiss = { showInterstitialAd = false }
    )

    if (showVipDialog) {
        BazaarVipSubscriptionDialog(
            onDismiss = { showVipDialog = false },
            onSuccess = { showVipDialog = false }
        )
    }

    // First Launch: Mandatory Disclaimer Screen
    if (!isDisclaimerAccepted) {
        DisclaimerScreen(
            onAccept = { viewModel.acceptDisclaimer() }
        )
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CryptoNavyDark,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Drawer Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "کیف پول یار",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "جعبه‌ابزار اخلاقی بازیابی",
                                fontSize = 11.sp,
                                color = CyanPrimary
                            )
                        }
                    }

                    HorizontalDivider(color = CryptoBorderDark)

                    // Drawer Items
                    DrawerNavRow("داشبورد اصلی", Icons.Default.Dashboard, currentTab == 0) {
                        viewModel.setTab(0)
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerNavRow("اعتبارسنجی عبارات (Seed)", Icons.AutoMirrored.Filled.FactCheck, currentTab == 1) {
                        viewModel.setTab(1)
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerNavRow("کلمات مفقود و ناخوانا", Icons.Default.Psychology, currentTab == 2) {
                        viewModel.setTab(2)
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerNavRow("استعلام موجودی بلاک‌چین", Icons.Default.AccountBalanceWallet, currentTab == 3) {
                        viewModel.setTab(3)
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerNavRow("مسیرهای اشتقاق (BIP-44/84)", Icons.Default.AccountTree, currentTab == 4) {
                        viewModel.setTab(4)
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerNavRow("فهرست تحت‌نظر (Watchlist)", Icons.Default.Bookmarks, currentTab == 5) {
                        viewModel.setTab(5)
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerNavRow("راهنمای گام‌به‌گام کیف‌پول‌ها", Icons.AutoMirrored.Filled.MenuBook, currentTab == 6) {
                        viewModel.setTab(6)
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerNavRow("🚨 پروتکل اضطراری نشت Seed", Icons.Default.WarningAmber, currentTab == 7, isAlert = true) {
                        viewModel.setTab(7)
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerNavRow("هشدارهای کلاهبرداری (Scam Alert)", Icons.Default.Shield, currentTab == 8) {
                        viewModel.setTab(8)
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerNavRow("تنظیمات و ظاهر برنامه", Icons.Default.Tune, currentTab == 9) {
                        viewModel.setTab(9)
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerNavRow("درباره ما و سلب مسئولیت", Icons.Default.Info, currentTab == 10) {
                        viewModel.setTab(10)
                        coroutineScope.launch { drawerState.close() }
                    }

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = "نسخه ۱.۰.۰ | سیدحمید موسوی زاده",
                        fontSize = 10.sp,
                        color = TextTertiary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_screen"),
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_app_logo),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "کیف پول یار (Wallet Yar)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = getTabTitle(currentTab),
                                    fontSize = 10.sp,
                                    color = CyanPrimary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "منو")
                        }
                    },
                    actions = {
                        // VIP Badge / Button
                        FilledTonalButton(
                            onClick = { showVipDialog = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isVip) Color(0xFF193B27) else GoldAccent.copy(alpha = 0.2f),
                                contentColor = if (isVip) GreenSuccess else GoldAccent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isVip) Icons.Default.WorkspacePremium else Icons.Default.Star,
                                contentDescription = "VIP",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isVip) "VIP بازار" else "VIP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Settings quick icon
                        IconButton(onClick = { viewModel.setTab(9) }) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "تنظیمات",
                                tint = if (currentTab == 9) CyanPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = (currentTab == 0),
                        onClick = { viewModel.setTab(0) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "داشبورد") },
                        label = { Text("داشبورد", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF003038),
                            selectedTextColor = CyanPrimary,
                            indicatorColor = CyanPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = (currentTab == 1),
                        onClick = {
                            viewModel.setTab(1)
                            if (!isVip && (0..3).random() == 0) showInterstitialAd = true
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = "اعتبارسنجی") },
                        label = { Text("اعتبارسنجی", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF003038),
                            selectedTextColor = CyanPrimary,
                            indicatorColor = CyanPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = (currentTab == 2),
                        onClick = {
                            viewModel.setTab(2)
                            if (!isVip && (0..3).random() == 0) showInterstitialAd = true
                        },
                        icon = { Icon(Icons.Default.Psychology, contentDescription = "مفقود") },
                        label = { Text("مفقود", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF003038),
                            selectedTextColor = CyanPrimary,
                            indicatorColor = CyanPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = (currentTab == 3),
                        onClick = { viewModel.setTab(3) },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "استعلام") },
                        label = { Text("استعلام", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF003038),
                            selectedTextColor = CyanPrimary,
                            indicatorColor = CyanPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = (currentTab == 7),
                        onClick = { viewModel.setTab(7) },
                        icon = { Icon(Icons.Default.WarningAmber, contentDescription = "اضطراری") },
                        label = { Text("اضطراری", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = RedAlert,
                            indicatorColor = RedAlert,
                            unselectedIconColor = RedAlert.copy(alpha = 0.7f),
                            unselectedTextColor = RedAlert.copy(alpha = 0.7f)
                        )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    0 -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.setTab(it) }
                    )
                    1 -> SeedValidatorScreen(viewModel = viewModel)
                    2 -> MissingWordsScreen(viewModel = viewModel)
                    3 -> BalanceScannerScreen(viewModel = viewModel)
                    4 -> DerivationPathScreen()
                    5 -> WatchlistScreen(
                        viewModel = viewModel,
                        onScanAddress = { addr ->
                            viewModel.onScannerAddressChange(addr)
                            viewModel.setTab(3)
                        }
                    )
                    6 -> RecoveryGuideScreen()
                    7 -> EmergencyScreen()
                    8 -> ScamAlertScreen()
                    9 -> SettingsScreen(viewModel = viewModel)
                    10 -> AboutScreen()
                    else -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.setTab(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerNavRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    isAlert: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        color = when {
            isSelected -> CyanPrimary.copy(alpha = 0.2f)
            isAlert -> RedAlert.copy(alpha = 0.15f)
            else -> Color.Transparent
        },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when {
                    isAlert -> RedAlert
                    isSelected -> CyanPrimary
                    else -> TextSecondary
                },
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isAlert -> RedAlert
                    isSelected -> CyanPrimary
                    else -> Color.White
                }
            )
        }
    }
}

private fun getTabTitle(tab: Int): String = when (tab) {
    0 -> "داشبورد ابزارها"
    1 -> "اعتبارسنجی ۱۲ و ۲۴ کلمه"
    2 -> "کشف کلمات مفقود و ناخوانا"
    3 -> "استعلام موجودی و تراکنش‌ها"
    4 -> "مسیرهای اشتقاق BIP-44/84"
    5 -> "فهرست تحت‌نظر (Watchlist)"
    6 -> "راهنمای بازیابی کیف‌پول‌ها"
    7 -> "🚨 پروتکل اضطراری نشت Seed"
    8 -> "شناسایی کلاهبرداری‌ها"
    9 -> "تنظیمات و شخصی‌سازی"
    10 -> "درباره سازنده و سلب مسئولیت"
    else -> "کیف پول یار"
}
