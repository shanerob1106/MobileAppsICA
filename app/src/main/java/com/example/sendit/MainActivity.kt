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
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.sendit.pages.interaction.getUserLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.auth.ktx.auth

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    private lateinit var auth: FirebaseAuth
    private var isUserLoggedIn by mutableStateOf(false)

    private var locationUpdateJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        isUserLoggedIn = auth.currentUser != null

        // Check if user is already logged in
        auth.addAuthStateListener { firebaseAuth ->
            isUserLoggedIn = firebaseAuth.currentUser != null

            if (isUserLoggedIn) {
                checkLocationPermissionAndStart()
            } else {
                stopUpdatingLocation()
            }
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

    private fun checkLocationPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startUpdatingLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startUpdatingLocation()
            } else {
                // Permission denied - maybe show a dialog explaining why it's needed
            }
        }

    private fun startUpdatingLocation() {
        if (locationUpdateJob?.isActive == true) return

        locationUpdateJob = coroutineScope.launch {
            val db = Firebase.firestore
            val auth = Firebase.auth
            val context = this@MainActivity

            while (true) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val location = getUserLocation(context)
                    location?.let { loc ->
                        val userDocRef = db.collection("users").document(currentUser.uid)
                        userDocRef.update(
                            mapOf(
                                "userLatitude" to loc.latitude,
                                "userLongitude" to loc.longitude
                            )
                        ).addOnSuccessListener {
                            // Successfully updated location
                        }.addOnFailureListener { e ->
                            // Handle error if needed
                        }
                    }
                }
                delay(120000L) // 2 minutes
            }
        }
    }

    private fun stopUpdatingLocation() {
        locationUpdateJob?.cancel()
    }
}


