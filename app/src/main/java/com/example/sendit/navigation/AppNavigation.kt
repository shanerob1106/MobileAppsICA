package com.example.sendit.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sendit.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class BottomNavItem(
    val label: String,
    val icon: (@Composable () -> Unit),
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                text = "Send It.",
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(color = MaterialTheme.colorScheme.primary)
            )
        },
        actions = {
            IconButton(onClick = {
                navController.navigate(Screen.AI.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ai_robot),
                    contentDescription = "AI Chat Page",
                )
            }

//            TODO: Add Chat Functions
//            IconButton(onClick = {
//                navController.navigate(Screen.Chat.route) {
//                    popUpTo(navController.graph.findStartDestination().id) {
//                        saveState = true
//                    }
//                    launchSingleTop = true
//                    restoreState = true
//                }
//            }) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Outlined.Send,
//                    contentDescription = "Chat",
//                    modifier = Modifier.size(24.dp)
//                )
//            }

            IconButton(onClick = {
                navController.navigate(Screen.UserMap.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = "Map",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {

    // Get the current user's ID
    val auth = Firebase.auth
    val userId = auth.currentUser?.uid

    // Bottom Navigation Items
    val items = listOf(
        BottomNavItem("Home", {
            Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
        }, Screen.Home.route),
        BottomNavItem("Search", {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        }, Screen.Search.route),
        BottomNavItem("Add", {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }, Screen.Add.route),
        BottomNavItem("Activities", {
            Icon(
                painter = painterResource(id = R.drawable.leaderboard_icon),
                contentDescription = "Activities"
            )
        }, Screen.Activities.route),
        BottomNavItem("Profile", {
            Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Profile")
        }, Screen.Profile.route + "/${userId}")
    )


    // Get the current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Bottom Navigation
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                icon = item.icon,
                label = { Text(text = item.label) }
            )
        }
    }
}