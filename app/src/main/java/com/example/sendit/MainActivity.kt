package com.example.sendit

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.sendit.ui.theme.SendItTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    private lateinit var auth: FirebaseAuth
    private var isUserLoggedIn by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        isUserLoggedIn = auth.currentUser != null

        // Check if user is already logged in
        auth.addAuthStateListener { firebaseAuth ->
            isUserLoggedIn = firebaseAuth.currentUser != null
        }

        enableEdgeToEdge()
        setContent {
            SendItTheme {
                val navController = rememberNavController()

                MainScaffold(
                    navController = navController,
                    isUserLoggedIn = isUserLoggedIn
                )
            }
        }
    }
}
