package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuitSmokingDashboard(
    viewModel: QuitSmokingViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.userSettings.collectAsState()
    val cravingLogs by viewModel.cravingLogs.collectAsState()
    val slipUpLogs by viewModel.slipUpLogs.collectAsState()
    val timeComponents by viewModel.timeComponents.collectAsState()
    val stats by viewModel.stats.collectAsState()

    var showEditSettings by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (settings == null) {
            // Setup / Onboarding form
            OnboardingSetupScreen(
                onSave = { quitDate, cigsPerDay, cigsPerPack, pricePerPack ->
                    viewModel.saveSettings(quitDate, cigsPerDay, cigsPerPack, pricePerPack)
                }
            )
        } else {
            val currentSettings = settings!!
            // Main Dashboard View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                // Restricted max width container for tablet ergonomics
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. Hero banner header with elegant greeting
                    DashboardHeroHeader(
                        onEditClick = { showEditSettings = !showEditSettings }
                    )

                    // Expandable settings editor
                    AnimatedVisibility(
                        visible = showEditSettings,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        SettingsEditorCard(
                            settings = currentSettings,
                            onSave = { quitDate, cigsPerDay, cigsPerPack, pricePerPack ->
                                viewModel.saveSettings(quitDate, cigsPerDay, cigsPerPack, pricePerPack)
                                showEditSettings = false
                            },
                            onCancel = { showEditSettings = false }
                        )
                    }

                    // 2. Active stopwatch/timer card
                    StopwatchCard(timeComponents = timeComponents)

                    // 3. Health & Savings Stats Cards
                    StatsSummaryGrid(
                        stats = stats,
                        currencySymbol = currentSettings.currencySymbol
                    )

                    // Sponsor Test Banner Ad
                    AdMobBanner()

                    // 4. Coping Mechanism: Deep Breathing Guide
                    DeepBreathingGuideCard()

                    // 5. Health Milestones Progress Card
                    HealthMilestonesCard(elapsedMillis = timeComponents.elapsedMillis)

                    // 6. Craving and Slip-Up Actions Panel
                    InteractiveActionsPanel(
                        onLogCraving = { intensity, trigger, notes ->
                            viewModel.logCraving(intensity, trigger, notes)
                        },
                        onLogSlipUp = { count, notes ->
                            viewModel.logSlipUp(count, notes)
                        }
                    )

                    // 7. Historical activity logs
                    ActivityHistoryCard(
                        cravingLogs = cravingLogs,
                        slipUpLogs = slipUpLogs,
                        onResetAll = { viewModel.resetAllData() }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun OnboardingSetupScreen(
    onSave: (Long, Int, Int, Double) -> Unit
) {
    var daysAgo by remember { mutableStateOf("0") }
    var hoursAgo by remember { mutableStateOf("0") }
    var cigarettesPerDay by remember { mutableStateOf(15f) }
    var cigarettesPerPack by remember { mutableStateOf(20f) }
    var pricePerPack by remember { mutableStateOf("12.50") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .align(Alignment.CenterHorizontally)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Brand Logo & Image Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_smoke_free_banner_1784236849550),
                        contentDescription = "Fresh beginning banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "A Fresh Start Awaits",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Fill in your details to start tracking your health recovery.",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text(
                text = "My Smoking Habits",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Setup Form Cards
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "When did you smoke your last cigarette?",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = daysAgo,
                            onValueChange = { if (it.all { char -> char.isDigit() }) daysAgo = it },
                            label = { Text("Days ago") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = hoursAgo,
                            onValueChange = { if (it.all { char -> char.isDigit() }) hoursAgo = it },
                            label = { Text("Hours ago") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Text(
                        text = "Keep as 0 for both if you are quitting right now!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cigarettes Smoked Daily",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${cigarettesPerDay.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = cigarettesPerDay,
                        onValueChange = { cigarettesPerDay = it },
                        valueRange = 1f..50f,
                        steps = 49
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cigarettes Per Pack",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${cigarettesPerPack.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = cigarettesPerPack,
                        onValueChange = { cigarettesPerPack = it },
                        valueRange = 5f..40f,
                        steps = 35
                    )

                    OutlinedTextField(
                        value = pricePerPack,
                        onValueChange = { pricePerPack = it },
                        label = { Text("Price Per Pack ($)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Currency") }
                    )
                }
            }

            Button(
                onClick = {
                    val days = daysAgo.toLongOrNull() ?: 0L
                    val hours = hoursAgo.toLongOrNull() ?: 0L
                    val totalMillisAgo = (days * 24 * 60 * 60 * 1000L) + (hours * 60 * 60 * 1000L)
                    val quitDate = System.currentTimeMillis() - totalMillisAgo
                    val price = pricePerPack.toDoubleOrNull() ?: 12.50

                    onSave(quitDate, cigarettesPerDay.toInt(), cigarettesPerPack.toInt(), price)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_journey_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Begin Smoke-Free Journey",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DashboardHeroHeader(
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.img_smoke_free_banner_1784236849550),
                contentDescription = "Success illustration banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Soft atmospheric gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Breathing Free",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "One moment at a time. You've got this!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .testTag("edit_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Edit habits settings",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsEditorCard(
    settings: UserSettings,
    onSave: (Long, Int, Int, Double) -> Unit,
    onCancel: () -> Unit
) {
    var cigarettesPerDay by remember { mutableStateOf(settings.cigarettesPerDay.toFloat()) }
    var cigarettesPerPack by remember { mutableStateOf(settings.cigarettesPerPack.toFloat()) }
    var pricePerPack by remember { mutableStateOf(settings.pricePerPack.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Update My Habits",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Daily cigarette count: ${cigarettesPerDay.toInt()}")
            }
            Slider(
                value = cigarettesPerDay,
                onValueChange = { cigarettesPerDay = it },
                valueRange = 1f..50f,
                steps = 49
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cigarettes per pack: ${cigarettesPerPack.toInt()}")
            }
            Slider(
                value = cigarettesPerPack,
                onValueChange = { cigarettesPerPack = it },
                valueRange = 5f..40f,
                steps = 35
            )

            OutlinedTextField(
                value = pricePerPack,
                onValueChange = { pricePerPack = it },
                label = { Text("Price per pack") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val price = pricePerPack.toDoubleOrNull() ?: settings.pricePerPack
                        onSave(
                            settings.quitDateMillis, // Keep the same original quit date
                            cigarettesPerDay.toInt(),
                            cigarettesPerPack.toInt(),
                            price
                        )
                    }
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
fun StopwatchCard(timeComponents: TimeComponents) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pulse Indicator Animation
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 0.7f,
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            Color.Green.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .align(Alignment.CenterVertically)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.Green.copy(alpha = 0.8f),
                                CircleShape
                            )
                    )
                }

                Text(
                    text = "SMOKE-FREE TIME ELAPSED",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            // Digital Grid Stopwatch UI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeDigitUnit(value = timeComponents.days, label = "Days")
                TimeDigitSeparator()
                TimeDigitUnit(value = timeComponents.hours, label = "Hours")
                TimeDigitSeparator()
                TimeDigitUnit(value = timeComponents.minutes, label = "Mins")
                TimeDigitSeparator()
                TimeDigitUnit(value = timeComponents.seconds, label = "Secs")
            }
        }
    }
}

@Composable
fun TimeDigitUnit(value: Long, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%02d", value),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = label.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TimeDigitSeparator() {
    Text(
        text = ":",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
    )
}

@Composable
fun StatsSummaryGrid(
    stats: DashboardStats,
    currencySymbol: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatItemCard(
            title = "Money Saved",
            value = String.format(Locale.getDefault(), "%s%.2f", currencySymbol, stats.moneySaved),
            subtext = "reclaimed capital",
            icon = Icons.Default.MonetizationOn,
            containerColor = MaterialTheme.colorScheme.surface,
            iconTint = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )

        StatItemCard(
            title = "Cigarettes Avoided",
            value = String.format(Locale.getDefault(), "%.1f", stats.cigarettesAvoided),
            subtext = "not inhaled",
            icon = Icons.Default.SmokeFree,
            containerColor = MaterialTheme.colorScheme.surface,
            iconTint = Color(0xFFFF5722),
            modifier = Modifier.weight(1f)
        )
    }

    // Full width life expectancy regained card
    val lifeHours = stats.lifeRegainedMinutes / 60.0
    val lifeDays = lifeHours / 24.0
    val lifeRegainedFormatted = when {
        lifeDays >= 1.0 -> String.format(Locale.getDefault(), "%.1fd", lifeDays)
        lifeHours >= 1.0 -> String.format(Locale.getDefault(), "%.1fh", lifeHours)
        else -> String.format(Locale.getDefault(), "%.0fm", stats.lifeRegainedMinutes)
    }

    StatItemCard(
        title = "Life Expectancy Regained",
        value = lifeRegainedFormatted,
        subtext = "Gain of ~11 minutes per cigarette avoided!",
        icon = Icons.Default.Favorite,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        iconTint = Color(0xFFE91E63),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun StatItemCard(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconTint.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun DeepBreathingGuideCard() {
    var isBreathingActive by remember { mutableStateOf(false) }
    var breathingPhase by remember { mutableStateOf("TAP TO BREATHE") }
    var phaseTimer by remember { mutableStateOf(0) }
    var circleScale by remember { mutableStateOf(1f) }

    // Coroutine managing breathing timing and animation states
    LaunchedEffect(isBreathingActive) {
        if (isBreathingActive) {
            while (isBreathingActive) {
                // Phase 1: Inhale (4s)
                breathingPhase = "INHALE..."
                for (i in 1..40) {
                    circleScale = 1f + (i / 40f) * 0.6f
                    phaseTimer = (i + 9) / 10
                    delay(100L)
                }
                // Phase 2: Hold (4s)
                breathingPhase = "HOLD..."
                for (i in 1..40) {
                    phaseTimer = (i + 9) / 10
                    delay(100L)
                }
                // Phase 3: Exhale (4s)
                breathingPhase = "EXHALE..."
                for (i in 40 downTo 1) {
                    circleScale = 1f + (i / 40f) * 0.6f
                    phaseTimer = (41 - i) / 10
                    delay(100L)
                }
                // Short break
                breathingPhase = "REST"
                delay(1000L)
            }
        } else {
            breathingPhase = "TAP TO START"
            circleScale = 1f
            phaseTimer = 0
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = circleScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessVeryLow),
        label = "circle_scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Craving Relief Guard",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Practice 4-4-4 breathing to dissolve severe cravings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .clickable { isBreathingActive = !isBreathingActive }
                    .testTag("breathing_circle"),
                contentAlignment = Alignment.Center
            ) {
                // Expanding glowing breathing circle
                Box(
                    modifier = Modifier
                        .size((100 * animatedScale).dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }

                // Inner breathing state labels
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = breathingPhase,
                        color = if (circleScale > 1.2f) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    if (isBreathingActive && phaseTimer > 0) {
                        Text(
                            text = "$phaseTimer",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = if (isBreathingActive) "Focus on the expanding circle. Deep breaths." else "Tap the circle above to initiate exercise.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HealthMilestonesCard(elapsedMillis: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Health Recovery",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time bodily repair timeline",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    val completedCount = HealthMilestoneProvider.milestones.count { it.isCompleted(elapsedMillis) }
                    Text(
                        text = "$completedCount/${HealthMilestoneProvider.milestones.size} Done",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HealthMilestoneProvider.milestones.forEach { milestone ->
                    val progress = milestone.getProgress(elapsedMillis)
                    val isCompleted = milestone.isCompleted(elapsedMillis)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
                                else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Dynamic Milestone Icons
                        val milestoneIcon = when (milestone.iconName) {
                            "Favorite" -> Icons.Default.Favorite
                            "Air" -> Icons.Default.Air
                            "Mood" -> Icons.Default.Mood
                            "SelfImprovement" -> Icons.Default.SelfImprovement
                            "DirectionsRun" -> Icons.Default.DirectionsRun
                            "LocalActivity" -> Icons.Default.LocalActivity
                            else -> Icons.Default.Security
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = milestoneIcon,
                                contentDescription = null,
                                tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = milestone.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isCompleted) "100%" else String.format(Locale.getDefault(), "%.0f%%", progress * 100f),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = milestone.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveActionsPanel(
    onLogCraving: (String, String, String) -> Unit,
    onLogSlipUp: (Int, String) -> Unit
) {
    var showCravingLog by remember { mutableStateOf(false) }
    var showSlipUpLog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    showCravingLog = !showCravingLog
                    showSlipUpLog = false
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("log_craving_toggle"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showCravingLog) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = if (showCravingLog) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Log Craving", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    showSlipUpLog = !showSlipUpLog
                    showCravingLog = false
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("log_slip_toggle"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (showSlipUpLog) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
                    contentColor = if (showSlipUpLog) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("I Slipped Up", fontWeight = FontWeight.Bold)
            }
        }

        // Expandable Craving Logger Form
        AnimatedVisibility(
            visible = showCravingLog,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            CravingLogFormCard(
                onSave = { intensity, trigger, notes ->
                    onLogCraving(intensity, trigger, notes)
                    showCravingLog = false
                },
                onCancel = { showCravingLog = false }
            )
        }

        // Expandable Slip-up Reset Form
        AnimatedVisibility(
            visible = showSlipUpLog,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            SlipUpFormCard(
                onSave = { count, notes ->
                    onLogSlipUp(count, notes)
                    showSlipUpLog = false
                },
                onCancel = { showSlipUpLog = false }
            )
        }
    }
}

@Composable
fun CravingLogFormCard(
    onSave: (String, String, String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedIntensity by remember { mutableStateOf("Medium") }
    var selectedTrigger by remember { mutableStateOf("Stress") }
    var cravingNotes by remember { mutableStateOf("") }

    val intensities = listOf("Low", "Medium", "Severe")
    val triggers = listOf("Stress", "Social", "Habit", "Boredom", "After Meal", "Other")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Log A Craving",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Intensity Selection
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Craving Intensity",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    intensities.forEach { intensity ->
                        val isSelected = selectedIntensity == intensity
                        val btnColor = when (intensity) {
                            "Low" -> Color(0xFF4CAF50)
                            "Medium" -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }

                        Button(
                            onClick = { selectedIntensity = intensity },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) btnColor else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(intensity, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Trigger Selection
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "What triggered it?",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(triggers) { trigger ->
                        val isSelected = selectedTrigger == trigger
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTrigger = trigger },
                            label = { Text(trigger) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Quick notes
            OutlinedTextField(
                value = cravingNotes,
                onValueChange = { cravingNotes = it },
                label = { Text("What did you do to resist? (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) { Text("Cancel") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onSave(selectedIntensity, selectedTrigger, cravingNotes) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Log")
                }
            }
        }
    }
}

@Composable
fun SlipUpFormCard(
    onSave: (Int, String) -> Unit,
    onCancel: () -> Unit
) {
    var cigsCount by remember { mutableStateOf("1") }
    var slipNotes by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Log A Slip-Up",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Slip-ups happen. Recovery is a journey, not a single test. Resetting your clock helps you remain honest and track triggers correctly.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = cigsCount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) cigsCount = it },
                    label = { Text("Cigarettes Smoked") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = slipNotes,
                    onValueChange = { slipNotes = it },
                    label = { Text("Context or Trigger") },
                    modifier = Modifier.weight(2f),
                    placeholder = { Text("e.g. stressful meeting") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) { Text("Cancel", color = MaterialTheme.colorScheme.onSurface) }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val count = cigsCount.toIntOrNull() ?: 1
                        onSave(count, slipNotes)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Restart Clock & Log", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ActivityHistoryCard(
    cravingLogs: List<CravingLog>,
    slipUpLogs: List<SlipUpLog>,
    onResetAll: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }
    var showResetConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "History & Insights",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = { showResetConfirmation = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All")
                }
            }

            // Confirmation dialogue
            if (showResetConfirmation) {
                AlertDialog(
                    onDismissRequest = { showResetConfirmation = false },
                    title = { Text("Are you absolutely sure?") },
                    text = { Text("This will delete your quit smoking date, habits settings, and all craving and slip-up logs permanently. This cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onResetAll()
                                showResetConfirmation = false
                            }
                        ) {
                            Text("Yes, Delete All", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetConfirmation = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (cravingLogs.isEmpty() && slipUpLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No history logged yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Log cravings or slip-ups to see insights.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Combine lists sorted by timestamp
                    val combinedList = remember(cravingLogs, slipUpLogs) {
                        val items = mutableListOf<Any>()
                        items.addAll(cravingLogs)
                        items.addAll(slipUpLogs)
                        items.sortByDescending {
                            when (it) {
                                is CravingLog -> it.timestamp
                                is SlipUpLog -> it.timestamp
                                else -> 0L
                            }
                        }
                        items
                    }

                    combinedList.forEach { item ->
                        when (item) {
                            is CravingLog -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                val chipColor = when (item.intensity) {
                                                    "Low" -> Color(0xFF4CAF50)
                                                    "Medium" -> Color(0xFFFF9800)
                                                    else -> Color(0xFFF44336)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(chipColor, CircleShape)
                                                )
                                                Text(
                                                    text = "${item.intensity} Craving Resisted",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = "Trigger: ${item.trigger}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            if (item.notes.isNotEmpty()) {
                                                Text(
                                                    text = item.notes,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = formatter.format(Date(item.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            is SlipUpLog -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = "Slip-Up Registered",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }
                                            Text(
                                                text = "Cigarettes Smoked: ${item.count}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            if (item.notes.isNotEmpty()) {
                                                Text(
                                                    text = "Context: ${item.notes}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = formatter.format(Date(item.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdMobBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "SUPPORT SPONSOR (TEST AD)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = "ca-app-pub-3940256099942544/6300978111" // Standard Google Test Banner Unit ID
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        }
    }
}

