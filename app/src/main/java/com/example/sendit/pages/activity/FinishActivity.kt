package com.example.sendit.pages.activity

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sendit.helpers.uploadActivity
import com.example.sendit.navigation.Screen
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinishActivity(
    routeType: RouteType,
    navController: NavController,
    maxAltitude: Float,
    activityTime: Long
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Delete Activity")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Activities.route) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Activity",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Route name and grade
            var routeNameText by rememberSaveable { mutableStateOf("") }
            var routeGradeText by rememberSaveable { mutableStateOf("") }
            var routeTriesText by rememberSaveable { mutableStateOf("") }
            var showGradeError by remember { mutableStateOf(false) }
            val context = LocalContext.current

            // Activity location
            var latitude by remember { mutableDoubleStateOf(0.0) }
            var longitude by remember { mutableDoubleStateOf(0.0) }
            var locationTagged by remember { mutableStateOf(false) }

            // Select Location
            LaunchedEffect(Unit) {
                navController.currentBackStackEntry?.savedStateHandle?.get<Pair<Double, Double>>("location")
                    ?.let { (lat, lng) ->
                        latitude = lat
                        longitude = lng
                        locationTagged = true
                        // Clear the saved state to avoid re-processing
                        navController.currentBackStackEntry?.savedStateHandle?.remove<Pair<Double, Double>>(
                            "location"
                        )
                    }
            }
            Text("Session Ended")

            OutlinedTextField(
                value = routeNameText,
                onValueChange = { routeNameText = it },
                label = { Text("Route Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = routeGradeText,
                onValueChange = { newGrade ->
                    if (newGrade.isEmpty() || newGrade.all { it.isDigit() }) {
                        routeGradeText = newGrade
                        showGradeError =
                            newGrade.toIntOrNull()?.let { it !in 1..17 } ?: false
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Route Grade (1-17)") },
                isError = showGradeError,
                supportingText = {
                    if (showGradeError) {
                        Text("Please enter a number between 1 and 17")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = routeTriesText,
                onValueChange = { newTries ->
                    if (newTries.isEmpty() || newTries.all { it.isDigit() }) {
                        routeTriesText = newTries
                        showGradeError =
                            newTries.toIntOrNull()?.let { it !in 1..10000 } ?: false
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Tries") },
                isError = showGradeError,
                supportingText = {
                    if (showGradeError) {
                        Text("Please enter a valid number")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Location", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (locationTagged) {
                            Column {
                                Text("Latitude: ${String.format("%.6f", latitude)}")
                                Text("Longitude: ${String.format("%.6f", longitude)}")
                            }
                        } else {
                            Text("No location tagged")
                        }

                        Button(
                            onClick = {
                                navController.navigate(Screen.Map.route)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Tag Location"
                            )
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Text(if (locationTagged) "Update" else "Tag")
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(onClick = {
                    when {
                        routeNameText.isEmpty() -> {
                            Toast.makeText(
                                context,
                                "Please enter a route name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        routeGradeText.isEmpty() -> {
                            Toast.makeText(
                                context,
                                "Please enter a route grade",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        routeGradeText.toIntOrNull() == null || routeGradeText.toInt() !in 1..17 -> {
                            Toast.makeText(
                                context,
                                "Please enter a valid grade between 1 and 17",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        !locationTagged -> {
                            Toast.makeText(
                                context,
                                "Please tag your location",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            val isFlashed = routeTriesText.toInt() == 1

                            uploadActivity(
                                routeType = routeType,
                                routeName = routeNameText,
                                routeGrade = routeGradeText,
                                routeTries = routeTriesText,
                                isFlashed = isFlashed,
                                maxAltitude = maxAltitude,
                                activityTime = activityTime,
                                longitude = longitude,
                                latitude = latitude,
                                timestamp = Timestamp.now(),
                                navController = navController
                            )
                        }
                    }
                }) {
                    Text("Post Activity")
                }
            }
        }
    }
}
