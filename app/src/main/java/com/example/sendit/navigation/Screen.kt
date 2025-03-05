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
import com.example.sendit.pages.interaction.LikePage
import com.example.sendit.pages.interaction.SearchPage

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Search : Screen("search")
    object Add : Screen("add")
    object AI : Screen("ai")
    object Profile : Screen("profile")
    object Likes : Screen("likes")
    object Chat : Screen("chat")
}


@Composable
fun SendItNavHost(
    navController: NavHostController,
    startDestination: String,
    isUserLoggedIn: Boolean,
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
            HomePage()
        }

        composable(Screen.Search.route) {
            SearchPage()
        }

        composable(Screen.Add.route) {
            AddPage(navController = navController)
        }

        composable(Screen.AI.route) {
            AIPage()
        }

        composable(Screen.Profile.route) {
            ProfilePage()
        }

        composable(Screen.Likes.route) {
            LikePage()
        }

        composable(Screen.Chat.route) {
            ChatPage()
        }
    }
}
