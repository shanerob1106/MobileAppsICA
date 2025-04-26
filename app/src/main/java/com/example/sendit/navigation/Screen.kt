package com.example.sendit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sendit.pages.HomePage
import com.example.sendit.pages.account.LoginPage
import com.example.sendit.pages.account.ProfilePage
import com.example.sendit.pages.activity.ActivitiesPage
import com.example.sendit.pages.activity.FinishActivity
import com.example.sendit.pages.activity.RouteType
import com.example.sendit.pages.activity.StartActivity
import com.example.sendit.pages.interaction.AIPage
import com.example.sendit.pages.interaction.ChatPage
import com.example.sendit.pages.interaction.ViewFriendsMap
import com.example.sendit.pages.interaction.MapScreen
import com.example.sendit.pages.interaction.SearchPage
import com.example.sendit.pages.interaction.ViewLocationScreen
import com.example.sendit.pages.interaction.returnSelectedLocation
import com.example.sendit.pages.post.AddPage
import com.example.sendit.pages.post.CommentPage

sealed class Screen(val route: String) {

    // Profile
    data object Login : Screen("login")
    data object Profile : Screen("profile")

    // Posts
    data object Home : Screen("home")
    data object Add : Screen("add")
    data object Comments : Screen("comments")

    // Features
    data object Search : Screen("search")
    data object AI : Screen("ai")
    data object Chat : Screen("chat")
    data object Map : Screen("map")
    data object UserMap : Screen("usermap")

    // Activities
    data object Activities : Screen("activities")
    data object FinishActivity : Screen("finishActivity")
    data object MapForFinishActivity : Screen("mapForFinishActivity")
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
            ViewFriendsMap()
        }

        composable(Screen.Map.route) {
            MapScreen { lat, lng ->
                returnSelectedLocation(navController, lat, lng)
            }
        }

        composable(Screen.Activities.route) {
            ActivitiesPage(navController = navController)
        }

        composable(Screen.Comments.route + "/{userId}/{postId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val postId = backStackEntry.arguments?.getString("postId")
            if (postId != null) {
                if (userId != null) {
                    CommentPage(postId = postId, userId = userId, navController = navController)
                }
            }
        }

        // Route logs
        composable(
            "startActivity/{routeType}",
            arguments = listOf(navArgument("routeType") { type = NavType.StringType })
        ) { backStackEntry ->
            val routeTypeStr =
                backStackEntry.arguments?.getString("routeType") ?: RouteType.BOULDER.name
            val routeType = RouteType.valueOf(routeTypeStr)

            StartActivity(
                routeType = routeType,
                navController = navController
            )
        }

        composable(
            route = Screen.FinishActivity.route + "/{routeType}/{maxAltitude}/{activityTime}",
            arguments = listOf(
                navArgument("routeType") { type = NavType.StringType },
                navArgument("maxAltitude") { type = NavType.FloatType },
                navArgument("activityTime") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val routeType = RouteType.valueOf(backStackEntry.arguments?.getString("routeType")!!)
            val maxAltitude = backStackEntry.arguments?.getFloat("maxAltitude")!!
            val activityTime = backStackEntry.arguments?.getLong("activityTime")!!

            FinishActivity(
                routeType = routeType,
                maxAltitude = maxAltitude,
                activityTime = activityTime,
                navController = navController
            )
        }

        composable(Screen.MapForFinishActivity.route) {
            MapScreen { lat, lng ->
                returnSelectedLocation(navController, lat, lng)
            }
        }

        composable(
            route = "viewLocation/{latitude}/{longitude}",
            arguments = listOf(
                navArgument("latitude") { type = NavType.StringType },
                navArgument("longitude") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val longitude =
                backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0

            ViewLocationScreen(
                navController = navController,
                latitude = latitude,
                longitude = longitude
            )
        }
    }
}
