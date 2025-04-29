package com.example.sendit.pages.activity

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sendit.data.ActivityData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale

enum class RouteType {
    BOULDER,
    SPORT,
    TRADITIONAL
}

@Composable
fun RouteTypeOptions(
    selectedRouteType: RouteType,
    onRouteTypeSelected: (RouteType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        RouteTypeButton(
            text = "Boulder",
            isSelected = selectedRouteType == RouteType.BOULDER,
            onClick = { onRouteTypeSelected(RouteType.BOULDER) },
            modifier = Modifier.weight(1f)
        )

        RouteTypeButton(
            text = "Sport",
            isSelected = selectedRouteType == RouteType.SPORT,
            onClick = { onRouteTypeSelected(RouteType.SPORT) },
            modifier = Modifier.weight(1f)
        )

        RouteTypeButton(
            text = "Trad",
            isSelected = selectedRouteType == RouteType.TRADITIONAL,
            onClick = { onRouteTypeSelected(RouteType.TRADITIONAL) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun RouteTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 5.dp),
        onClick = onClick,
        colors = if (isSelected) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    ) {
        Text(
            text = text,
            maxLines = 1
        )
    }
}

@Composable
fun ActivityCard(
    activity: ActivityData?,
    onDeleteClick: () -> Unit,
    onLocationClick: (Double, Double) -> Unit
) {
    // Default values in case activity is null (for preview purposes)
    val routeName = activity?.routeName ?: "Climb Name"
    val date = activity?.timeStamp?.toDate()?.let {
        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(it)
    } ?: "01/01/2025"

    val grade = activity?.routeGrade ?: "V0"

    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Column {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = routeName,
                            fontSize = 18.sp,
                            style = TextStyle(color = MaterialTheme.colorScheme.primary)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = date,
                            fontSize = 12.sp,
                            style = TextStyle(color = MaterialTheme.colorScheme.primary)
                        )
                    }

                    Column {
                        if (activity!!.isFlashed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Flashed",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Not Flashed",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Activity",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                // Climb Grade
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "V" + grade,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    // Total Ascents
                    Text(
                        text = "Tries",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )

                    Text(
                        text = activity!!.routeTries,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = "Height",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )

                    Text(
                        text = activity!!.maxAltitude.toDouble().routeDouble().toString() + "m",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = "Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )

                    Text(
                        text = formatTime(activity!!.activityTime),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                }

                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            if (activity != null) {
                                onLocationClick(activity.latitude, activity.longitude)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Add"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityStatsCard(
    routeType: RouteType,
    activities: List<ActivityData>
) {
    // Calculate statistics from activities
    val avgGrade = calculateAverageGrade(activities)
    val totalFlashes = activities.count { it.isFlashed }
    val totalAscents = activities.size
    val totalHeight = activities.sumOf { it.maxAltitude.toDouble() }
    val totalTime = activities.sumOf { it.activityTime }
    val formattedTime = formatTime(totalTime)

    // Main Stats Card
    Card(
        modifier = Modifier
            .padding(5.dp)
            .padding(bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Column()
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Route Type Header
                Text(
                    text = "${
                        routeType.name.lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    } - Activity",
                    fontSize = 28.sp,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary)
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Display Average Grade
                Box(
                    modifier = Modifier
                        .size(75.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avgGrade,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                // Total Flashes Column
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Flashes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = totalFlashes.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Total Climbs Column
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Climbs",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = totalAscents.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                // Total Height Column
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Height: ${totalHeight.routeDouble()}m"
                    )
                }

                // Total Time Column
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Time: $formattedTime"
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityContent(
    routeType: RouteType,
    navController: NavController,
    viewModel: ActivityViewModel = remember { ActivityViewModel() }
) {
    val auth = Firebase.auth
    val uid = auth.currentUser?.uid
    val activities by viewModel.activities

    var activityToDelete by remember { mutableStateOf<ActivityData?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load activities when route type changes
    DisposableEffect(routeType) {
        viewModel.loadActivities(routeType)
        onDispose { }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && activityToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                activityToDelete = null
            },
            title = { Text("Delete Activity") },
            text = {
                Text(
                    "Are you sure you want to delete this ${
                        routeType.name.lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    } activity?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        activityToDelete?.let {
                            viewModel.deleteActivity(
                                userId = uid!!,
                                activityId = it.activityId,
                                routeType = routeType
                            )
                        }
                        showDeleteDialog = false
                        activityToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        activityToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            ActivityStatsCard(routeType = routeType, activities = activities)
        }

        items(activities) { activity ->
            ActivityCard(
                activity,
                onDeleteClick = {
                    activityToDelete = activity
                    showDeleteDialog = true
                },
                onLocationClick = { lat, lng ->
                    navController.navigate("viewLocation/$lat/$lng")
                }
            )
        }
    }
}


@Composable
fun ActivitiesPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var selectedRouteType by remember { mutableStateOf(RouteType.BOULDER) }
    val viewModel = remember { ActivityViewModel() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Activities Page",
                    fontSize = 28.sp,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                RouteTypeOptions(
                    selectedRouteType = selectedRouteType,
                    onRouteTypeSelected = { selectedRouteType = it }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("startActivity/${selectedRouteType.name}")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Activity"
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Display content based on selected route type, passing the shared viewModel
            ActivityContent(
                routeType = selectedRouteType,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

// Function to calculate average climbing grade
private fun calculateAverageGrade(activities: List<ActivityData>): String {
    if (activities.isEmpty()) return "V0"

    val grades = activities.mapNotNull { activity ->
        activity.routeGrade.takeIf { it.isNotEmpty() && it != "No Grade" }?.let {
            try {
                // Grade format "V1", "V2"
                it.removePrefix("V").toIntOrNull() ?: 0
            } catch (e: Exception) {
                0
            }
        } ?: 0
    }

    return if (grades.isEmpty()) "V0"
    else "V${(grades.sum().toFloat() / grades.size).toInt()}"
}

// Time format
@SuppressLint("DefaultLocale")
private fun formatTime(totalMilliseconds: Long): String {
    val totalSeconds = totalMilliseconds / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

@SuppressLint("DefaultLocale")
private fun Double.routeDouble(): Double {
    return String.format("%.2f", this).toDouble()
}