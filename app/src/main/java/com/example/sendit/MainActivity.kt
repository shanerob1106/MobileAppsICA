package com.example.sendit

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.sendit.pages.interaction.getUserLocation
import com.example.sendit.ui.theme.SendItTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "MainActivity"
private const val LOCATION_UPDATE_INTERVAL = 120000L // 2 minutes

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private var isUserLoggedIn by mutableStateOf(false)

    private var locationUpdateJob: Job? = null

    // Permission launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startUpdatingLocation()
            } else {
                Log.w("MainActivity", "Location permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        isUserLoggedIn = auth.currentUser != null

        // Setup auth state listener
        auth.addAuthStateListener { firebaseAuth ->
            val userLoggedIn = firebaseAuth.currentUser != null
            if (userLoggedIn != isUserLoggedIn) {
                isUserLoggedIn = userLoggedIn

                if (isUserLoggedIn) {
                    checkLocationPermissionAndStart()
                } else {
                    stopUpdatingLocation()
                }
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

        // Start location updates if user is already logged in
        if (isUserLoggedIn) {
            checkLocationPermissionAndStart()
        }
    }

    override fun onStart() {
        super.onStart()
        // Resume location updates if user is logged in
        if (isUserLoggedIn && locationUpdateJob?.isActive != true) {
            checkLocationPermissionAndStart()
        }
    }

    override fun onStop() {
        super.onStop()
        // Pause location updates when app is in background
        stopUpdatingLocation()
    }

    // Check location permissions and then start
    private fun checkLocationPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startUpdatingLocation()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                lifecycleScope.launch {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // Start updating the user location
    @SuppressLint("RepeatOnLifecycleWrongUsage")
    private fun startUpdatingLocation() {
        locationUpdateJob?.cancel()
        locationUpdateJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val currentUser = auth.currentUser ?: return@repeatOnLifecycle

                try {
                    while (true) {
                        updateUserLocation(currentUser.uid)
                        delay(LOCATION_UPDATE_INTERVAL)
                    }
                } catch (e: CancellationException) {
                    Log.d(TAG, "Location updates cancelled")
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating location", e)
                }
            }
        }
    }

    // Update the user location
    private suspend fun updateUserLocation(userId: String) {
        try {
            val db = Firebase.firestore
            val location = getUserLocation(this)

            if (location != null) {
                val userDocRef = db.collection("users").document(userId)
                userDocRef.update(
                    mapOf(
                        "userLatitude" to location.latitude,
                        "userLongitude" to location.longitude,
                        "lastLocationUpdate" to System.currentTimeMillis()
                    )
                ).await()
                Log.d(TAG, "Location updated successfully")
            } else {
                Log.w(TAG, "Could not get user location")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update location", e)
        }
    }

    // Stop updating the user location
    private fun stopUpdatingLocation() {
        locationUpdateJob?.cancel()
        locationUpdateJob = null
    }
}