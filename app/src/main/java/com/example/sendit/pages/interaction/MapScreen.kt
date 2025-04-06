package com.example.sendit.pages.interaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    onLocationSelected: (Double, Double) -> Unit = { _, _ -> } // Default empty callback
) {
    // Default location
    val defaultLocation = LatLng(0.0, 0.0)

    // State for the marker position
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }

    // Remember camera position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Map takes most of the screen
        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    // Update marker position when map is clicked
                    markerPosition = latLng
                }
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

            // Display coordinates at the bottom of the map
//            markerPosition?.let { position ->
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(bottom = 16.dp)
//                ) {
//                    Text(
//                        text = "Lat: ${position.latitude}, Lng: ${position.longitude}",
//                        modifier = Modifier.padding(8.dp)
//                    )
//                }
//            }
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

fun returnSelectedLocation(navController: NavController, latitude: Double, longitude: Double) {
    navController.previousBackStackEntry?.savedStateHandle?.set("location", Pair(latitude, longitude))
    navController.popBackStack()
}