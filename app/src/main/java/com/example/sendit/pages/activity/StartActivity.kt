package com.example.sendit.pages.activity

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sendit.helpers.SenseBarometer
import com.example.sendit.navigation.Screen
import kotlinx.coroutines.delay
import kotlin.math.pow

enum class SessionState {
    Idle, Calibrating, Tracking, Paused, Stopped
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartActivity(
    routeType: RouteType,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Start ${
                            routeType.name.lowercase().replaceFirstChar { it.uppercase() }
                        } Activity"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // Pressure values for calibration
            var startPressure by remember { mutableFloatStateOf(0f) }
            val pressureList = remember { mutableStateListOf<Float>() }
            var pressure by remember { mutableFloatStateOf(0f) }

            // Altitude values for tracking
            var currentAltitude by remember { mutableFloatStateOf(0f) }
            var startAltitude by remember { mutableFloatStateOf(0f) }
            var maxAltitude by remember { mutableFloatStateOf(0f) }
            val altitudeList = remember { mutableStateListOf<Float>() }

            // Frozen values for when session is paused
            var frozenCurrentAltitude by remember { mutableFloatStateOf(0f) }

            // Timer and session state
            var sessionState by remember { mutableStateOf(SessionState.Idle) }
            var timer by remember { mutableLongStateOf(0L) }

            // Size of calibration window (Higher = More accurate?)
            val calibrationSize = 25

            // Convert pressure to altitude
            fun pressureToAltitude(p: Float): Float {
                return 44330f * (1f - (p / startPressure).toDouble().pow(1.0 / 5.255)).toFloat()
            }

            // Auto-start tracking once calibration completes
            if (sessionState == SessionState.Calibrating && pressureList.size >= calibrationSize) {
                LaunchedEffect(pressureList.size) {
                    startPressure = pressureList.average().toFloat()
                    startAltitude = pressureToAltitude(startPressure)
                    sessionState = SessionState.Tracking
                    timer = 0L
                }
            }

            // Timer for tracking session
            if (sessionState == SessionState.Tracking) {
                LaunchedEffect(Unit) {
                    while (sessionState == SessionState.Tracking) {
                        delay(1000)
                        timer += 1000
                    }
                }
            }

            // Barometer Sensor
            SenseBarometer(
                onPressureChanged = { newPressure ->
                    pressure = newPressure

                    if (sessionState == SessionState.Calibrating && pressureList.size < calibrationSize) {
                        pressureList.add(newPressure)
                    }

                    if (startPressure > 0f) {
                        currentAltitude = pressureToAltitude(newPressure)

                        if (sessionState == SessionState.Tracking) {
                            altitudeList.add(currentAltitude)
                            if (currentAltitude > maxAltitude) {
                                maxAltitude = currentAltitude
                            }
                        }
                    }
                }
            )

            // Utility for formatting timer
            fun formatTime(ms: Long): String {
                val totalSeconds = ms / 1000
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                return "%02d:%02d:%02d".format(hours, minutes, seconds)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fitness tracking card UI
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Timer
                    Text(
                        text = formatTime(timer),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Altitude info
                    val displayAltitude =
                        if (sessionState == SessionState.Tracking) currentAltitude else frozenCurrentAltitude

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Current Altitude: ${String.format("%.2f", displayAltitude)} m")
                        Text("Max Altitude: ${String.format("%.2f", maxAltitude)} m")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Session Controls
            when (sessionState) {
                SessionState.Idle -> {
                    Button(onClick = {
                        pressureList.clear()
                        altitudeList.clear()
                        startPressure = 0f
                        startAltitude = 0f
                        currentAltitude = 0f
                        maxAltitude = 0f
                        timer = 0L
                        frozenCurrentAltitude = 0f
                        sessionState = SessionState.Calibrating
                    }) {
                        Text("Start")
                    }
                }

                // Session Calibration
                SessionState.Calibrating -> {
                    Text("Calibrating barometer... (${pressureList.size}/${calibrationSize})")
                    LinearProgressIndicator(
                        progress = { pressureList.size / calibrationSize.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

                // Session Active
                SessionState.Tracking -> {
                    Button(onClick = {
                        frozenCurrentAltitude = currentAltitude
                        sessionState = SessionState.Paused
                    }) {
                        Text("Pause")
                    }
                }

                // Session Paused
                SessionState.Paused -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { sessionState = SessionState.Tracking }) {
                            Text("Resume")
                        }
                        Button(onClick = {
                            frozenCurrentAltitude = currentAltitude
                            sessionState = SessionState.Stopped
                        }) {
                            Text("Stop")
                        }
                    }
                }

                // Session Ended
                SessionState.Stopped -> {
                    navController.navigate(
                        "${Screen.FinishActivity.route}/${routeType.name}/${maxAltitude}/${timer}"
                    )
                }
            }
        }
    }
}
