package com.example.sendit.pages.interaction

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

@Composable
fun CurrentUserLocation() {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var locationError by remember { mutableStateOf<String?>(null) }

    // Check location permission
    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Get user's current location
    LaunchedEffect(key1 = hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                getUserLocation(context)?.let { location ->
                    userLocation = LatLng(location.latitude, location.longitude)
                } ?: run {
                    locationError = "Unable to get current location"
                }
            } catch (e: Exception) {
                locationError = "Error getting location: ${e.message}"
            } finally {
                isLoading = false
            }
        } else {
            locationError = "Location permission not granted"
            isLoading = false
        }
    }

    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: LatLng(0.0, 0.0), // Default to (0,0) if user location not available
            userLocation?.let { 15f } ?: 2f // Zoom level 15 for user location, 2 for world view
        )
    }

    // Update camera position when user location is obtained
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Show loading indicator or error message
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            locationError != null -> {
                Text(
                    text = locationError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            else -> {
                // Map with user location
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = hasLocationPermission)
                ) {
                    // Show marker at user's location
                    userLocation?.let { position ->
                        Marker(
                            state = MarkerState(position),
                            title = "Your Location",
                            snippet = "Lat: ${position.latitude}, Lng: ${position.longitude}"
                        )
                    }
                }
            }
        }
    }
}

// Function to get the user's current location
suspend fun getUserLocation(context: Context): LatLng? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return null // Permission not granted
    }

    return try {
        val lastLocation = fusedLocationClient.lastLocation.await()
        lastLocation?.let { LatLng(it.latitude, it.longitude) }
    } catch (e: Exception) {
        null
    }
}

@Composable
fun MapScreen(
    onLocationSelected: (Double, Double) -> Unit = { _, _ -> } // Default empty callback
) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }

    // Check location permission
    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Get user's current location
    LaunchedEffect(key1 = hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                getUserLocation(context)?.let { location ->
                    userLocation = LatLng(location.latitude, location.longitude)
                } ?: run {
                    locationError = "Unable to get current location"
                }
            } catch (e: Exception) {
                locationError = "Error getting location: ${e.message}"
            } finally {
                isLoading = false
            }
        } else {
            locationError = "Location permission not granted"
            isLoading = false
        }
    }

    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: LatLng(0.0, 0.0), // Default to (0,0) if user location not available
            userLocation?.let { 15f } ?: 2f // Zoom level 15 for user location, 2 for world view
        )
    }

    // Update camera position when user location is obtained
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Show loading indicator or error message
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            locationError != null -> {
                Text(
                    text = locationError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Map takes most of the screen
                    Box(modifier = Modifier.weight(1f)) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            onMapClick = { latLng ->
                                // Update marker position when map is clicked
                                markerPosition = latLng
                            },
                            properties = MapProperties(isMyLocationEnabled = hasLocationPermission)
                        ) {
                            // Show marker at clicked position if exists
                            markerPosition?.let { position ->
                                Marker(
                                    state = MarkerState(position),
                                    title = "Selected Location",
                                    snippet = "Lat: ${position.latitude}, Lng: ${position.longitude}"
                                )
                            }
                        }
                    }

                    // Button to use the selected location
                    markerPosition?.let { position ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                // Call the callback with selected coordinates
                                onLocationSelected(position.latitude, position.longitude)
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Use This Location")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

fun returnSelectedLocation(navController: NavController, latitude: Double, longitude: Double) {
    navController.previousBackStackEntry?.savedStateHandle?.set(
        "location",
        Pair(latitude, longitude)
    )
    navController.popBackStack()
}

// SAMPLE CODE - CODE THAT WAS PREVIOUSLY IN THE MAP SCREEN

// Display coordinates at the bottom
//                userLocation?.let { position ->
//                    Text(
//                        text = "Your Location: ${
//                            String.format(
//                                "%.6f",
//                                position.latitude
//                            )
//                        }, ${String.format("%.6f", position.longitude)}",
//                        color = MaterialTheme.colorScheme.onSurface,
//                        fontSize = 12.sp,
//                        modifier = Modifier
//                            .align(Alignment.BottomCenter)
//                            .padding(bottom = 16.dp)
//                            .padding(horizontal = 16.dp)
//                    )
//                }