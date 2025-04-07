package com.example.sendit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sendit.navigation.BottomNavigationBar
import com.example.sendit.navigation.Screen
import com.example.sendit.navigation.SendItNavHost
import com.example.sendit.navigation.TopAppBar

@Composable
fun MainScaffold(
    navController: NavHostController = rememberNavController(),
    isUserLoggedIn: Boolean
) {
    // Determine starting destination based on login state
    val startDestination = if (isUserLoggedIn) Screen.Home.route else Screen.Login.route

    // Get current route to determine if we should show navigation
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Launch effect to navigate to home when user logs in
    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn && currentRoute == Screen.Login.route) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else if (!isUserLoggedIn && currentRoute != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Show bars on all screens except login screen
    val showBars = isUserLoggedIn && currentRoute != Screen.Login.route

    Scaffold(
        topBar = {
            if (showBars) {
                TopAppBar(navController)
            }
        },
        bottomBar = {
            if (showBars) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        SendItNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        )
    }
}