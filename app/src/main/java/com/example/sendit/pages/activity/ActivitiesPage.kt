package com.example.sendit.pages.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
            onClick = { onRouteTypeSelected(RouteType.BOULDER) }
        )

        RouteTypeButton(
            text = "Sport",
            isSelected = selectedRouteType == RouteType.SPORT,
            onClick = { onRouteTypeSelected(RouteType.SPORT) }
        )

        RouteTypeButton(
            text = "Traditional",
            isSelected = selectedRouteType == RouteType.TRADITIONAL,
            onClick = { onRouteTypeSelected(RouteType.TRADITIONAL) }
        )
    }
}

@Composable
fun RouteTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .padding(5.dp),
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
        Text(text = text)
    }
}

@Composable
fun ClimbCard() {
    Card(
        modifier = Modifier
            .padding(5.dp)
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
                    text = "Climb Name",
                    fontSize = 18.sp,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary)
                )
            }

            Row(
                modifier = Modifier
                    .padding(10.dp)
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
fun RouteStatsCard() {
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
                    text = "Ability",
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
fun ActivityContent(routeType: RouteType) {
    when (routeType) {
        RouteType.BOULDER -> {
            LazyColumn {
                item {
                    RouteStatsCard()
                }

                items(5) { index ->
                    ClimbCard()
                }
            }
        }

        RouteType.SPORT -> {
            LazyColumn {
                item {
                    RouteStatsCard()
                }

                items(3) { index ->
                    ClimbCard()
                }
            }
        }

        RouteType.TRADITIONAL -> {
            LazyColumn {
                item {
                    RouteStatsCard()
                }
                items(4) { index ->
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

//    val uid = Firebase.auth.currentUser?.uid.toString()
//    setupRoutes(uid)

    var selectedRouteType by remember { mutableStateOf(RouteType.BOULDER) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Activities Page",
                    fontSize = 28.sp,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary)
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
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
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