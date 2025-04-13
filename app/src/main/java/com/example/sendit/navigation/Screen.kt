package com.example.sendit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sendit.pages.HomePage
import com.example.sendit.pages.account.LoginPage
import com.example.sendit.pages.account.ProfilePage
import com.example.sendit.pages.interaction.AIPage
import com.example.sendit.pages.interaction.AddPage
import com.example.sendit.pages.interaction.ChatPage
import com.example.sendit.pages.interaction.CommentPage
import com.example.sendit.pages.interaction.CurrentUserLocation
import com.example.sendit.pages.interaction.ActivitiesPage
import com.example.sendit.pages.interaction.MapScreen
import com.example.sendit.pages.interaction.SearchPage
import com.example.sendit.pages.interaction.returnSelectedLocation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Add : Screen("add")
    data object AI : Screen("ai")
    data object Profile : Screen("profile")
    data object Chat : Screen("chat")
    data object Map : Screen("map")
    data object UserMap : Screen("usermap")
    data object Comments : Screen("comments")
    data object Activities : Screen("activities")
}


@Composable
fun SendItNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route)
        {
            LoginPage(
                onLoginSuccess = {

                }
            )
        }

        composable(Screen.Home.route) {
            HomePage(navController = navController)
        }

        composable(Screen.Search.route) {
            SearchPage(navController = navController)
        }

        composable(Screen.Add.route) {
            AddPage(navController = navController)
        }

        composable(Screen.AI.route) {
            AIPage()
        }

        composable(Screen.Profile.route + "/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ProfilePage(navController = navController, profileUserId = userId)
        }

        composable(Screen.Chat.route) {
            ChatPage()
        }

        composable(Screen.UserMap.route) {
            CurrentUserLocation()
        }

        composable(Screen.Map.route) {
            MapScreen { lat, lng ->
                returnSelectedLocation(navController, lat, lng)
            }
        }

        composable(Screen.Activities.route) {
            ActivitiesPage()
        }

        composable(Screen.Comments.route + "/{userId}/{postId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val postId = backStackEntry.arguments?.getString("postId")
            if (postId != null) {
                if (userId != null) {
                    CommentPage(postId = postId, userId = userId)
                }
            }
        }
    }
}
