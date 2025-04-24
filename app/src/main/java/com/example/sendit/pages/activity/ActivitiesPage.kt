package com.example.sendit.pages.activity

import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

enum class RouteType {
    BOULDER,
    SPORT,
    TRADITIONAL
}

@Composable
fun RouteStyles(
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
            text = "Traditional",
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
fun ClimbCard(
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
                        val isFlashed = true
                        if (isFlashed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Flashed",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Not Flashed",
                                tint = MaterialTheme.colorScheme.secondary
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
                // Placeholder for Climb Grade
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
                    // Total Flashes
                    Text(
                        text = "Flashed",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )
                    // Flashes
                    Text(
                        text = "YES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
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
                    // Ascents
                    Text(
                        text = "1",
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
fun RouteStatsCard(
    routeType: RouteType
) {
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
                Text(
                    text = "${routeType.name.lowercase().capitalize()} - Activity",
                    fontSize = 28.sp,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary)
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Placeholder for Avg. Climb Grade
                Box(
                    modifier = Modifier
                        .size(75.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder text Hueco V0
                    Text(
                        text = "V0",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    // Total Flashes
                    Text(
                        text = "Flashes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )
                    // Flashes
                    Text(
                        text = "0",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    // Total Ascents
                    Text(
                        text = "Ascents",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )
                    // Ascents
                    Text(
                        text = "0",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Total Height Climbed
                    Text(
                        text = "Height: " + "0m"
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Total Time Spent Climbing
                    Text(
                        text = "Time: " + "00:00"
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityContent(
    routeType: RouteType,
    navController: NavController
) {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val uid = auth.currentUser?.uid
    var activities by remember { mutableStateOf(emptyList<ActivityData>()) }

    var activityToDelete by remember { mutableStateOf <ActivityData?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteDialog && activityToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                activityToDelete = null
            },
            title = { Text("Delete Activity") },
            text = { Text("Are you sure you want to delete this " + routeType.name.lowercase().capitalize() + " activity?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        activityToDelete?.let { deleteActivity(userId = uid!!, activityId = it.activityId, routeType = routeType) }
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

    // Set up Firestore listener using DisposableEffect
    DisposableEffect(routeType) {
        val listener = uid?.let { userId ->
            db.collection("users")
                .document(userId)
                .collection("activities")
                .document(routeType.name.uppercase())
                .collection("sessions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ActivityContent", "Error loading activities", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val sortedActivities = snapshot.documents.mapNotNull { doc ->
                            val activityId = doc.id
                            val routeName = doc.getString("routeName") ?: "No Route Name"
                            val date = doc.getTimestamp("timestamp")
                            val grade = doc.getString("routeGrade") ?: "No Grade"
                            val time = doc.getLong("activityTime") ?: 0L
                            val maxAltitude = doc.getDouble("maxAltitude")?.toFloat() ?: 0f

                            ActivityData(
                                activityId = activityId,
                                routeName = routeName,
                                timeStamp = date,
                                routeGrade = grade,
                                activityTime = time,
                                maxAltitude = maxAltitude,
                                latitude = doc.getDouble("latitude") ?: 0.0,
                                longitude = doc.getDouble("longitude") ?: 0.0
                            )
                        }.sortedByDescending { it.timeStamp }

                        activities = sortedActivities
                    }
                }
        }

        onDispose {
            listener?.remove()
        }
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            RouteStatsCard(routeType)
        }

        items(activities) { activity ->
            ClimbCard(
                activity,
                onDeleteClick = {
                    activityToDelete = activity
                    showDeleteDialog = true
                },
                onLocationClick = {lat, lng ->
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
                RouteStyles(
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
        // Fix the gap by setting appropriate bottom padding
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Display content based on selected route type
            ActivityContent(routeType = selectedRouteType, navController = navController)
        }
    }
}

// Delete Activity
fun deleteActivity(
    routeType: RouteType,
    userId: String,
    activityId: String
) {
    val db = Firebase.firestore

    db.collection("users")
        .document(userId)
        .collection("activities")
        .document(routeType.name)
        .collection("sessions")
        .document(activityId)
        .delete()
}