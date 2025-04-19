package com.example.sendit.pages.activity

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun ClimbCard() {
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
                            text = "Climb Name",
                            fontSize = 18.sp,
                            style = TextStyle(color = MaterialTheme.colorScheme.primary)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = "01/01/2025", // Date Format: DD/MM/YYYY
                            fontSize = 18.sp,
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
                            onClick = {/*TODO: Delete a logged activity*/ },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Post",
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
                        onClick = { /* Handle button click */ }
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
    routeType: RouteType
) {
    when (routeType) {
        RouteType.BOULDER, RouteType.SPORT, RouteType.TRADITIONAL -> {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Extra padding at bottom to avoid FAB overlap
            ) {
                item {
                    RouteStatsCard(routeType)
                }

                // Dynamic number of items based on route type
                val itemCount = when (routeType) {
                    RouteType.BOULDER -> 5
                    RouteType.SPORT -> 3
                    RouteType.TRADITIONAL -> 4
                }

                items(itemCount) { index ->
                    ClimbCard()
                }
            }
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
            ActivityContent(routeType = selectedRouteType)
        }
    }
}

// Spare Code - May have a use
//fun setupRoutes(
//    uid: String
//){
//    val db = Firebase.firestore
//
//    val routes = db.collection("users")
//        .document(uid)
//        .collection("routes")
//        .get()
//
//    if(routes.isSuccessful){
//        Log.d("routes", "routes: ${routes.result}")
//    } else {
//        Log.w("routes", "routes: ${routes.exception}, no routes found.")
//        val addBoulder = db.collection("users")
//            .document(uid)
//            .collection("routes")
//            .add("boulder")
//        val addSport = db.collection("users")
//            .document(uid)
//            .collection("routes")
//            .add("sport")
//        val addTrad = db.collection("users")
//            .document(uid)
//            .collection("routes")
//            .add("traditional")
//    }
//}