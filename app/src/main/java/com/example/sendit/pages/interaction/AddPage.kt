package com.example.sendit.pages.interaction

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.example.sendit.navigation.Screen
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@Composable
fun AddPage(navController: NavController) {
    val context = LocalContext.current

    var captionText by rememberSaveable { mutableStateOf("") }
    val imageUris = rememberSaveable { mutableStateOf<List<Uri?>?>(null) }

    var selectedLatitude by rememberSaveable { mutableStateOf(0.0) }
    var selectedLongitude by rememberSaveable { mutableStateOf(0.0) }
    var isLocationSelected by rememberSaveable { mutableStateOf(false) }

    var hasLaunchedPicker by rememberSaveable { mutableStateOf(false) }

    var isUploading by rememberSaveable { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
            imageUris.value = it
        }

    LaunchedEffect(Unit) {
        if (!hasLaunchedPicker && imageUris.value == null) {
            hasLaunchedPicker = true
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    LaunchedEffect(currentBackStackEntry) {
        currentBackStackEntry?.savedStateHandle?.get<Pair<Double, Double>?>("location")
            ?.let { (lat, lng) ->
                selectedLatitude = lat
                selectedLongitude = lng
                isLocationSelected = true
                currentBackStackEntry.savedStateHandle.remove<Pair<Double, Double>>("location")
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text(
                text = "Create Post",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            imageUris.value?.takeIf { it.isNotEmpty() }?.let { images ->
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(images) { uri ->
                        val painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context).data(uri).build()
                        )
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(vertical = 4.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    label = { Text("Add a caption...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        navController.navigate("map")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📍 Select Location")
                }

                if (isLocationSelected) {
                    Text(
                        text = "Selected Location: ($selectedLatitude, $selectedLongitude)",
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            } ?: Text("Please select images to begin.")
        }

        Button(
            onClick = {
                val images = imageUris.value ?: return@Button
                if (images.isNotEmpty() && !isUploading) {
                    isUploading = true
                    uploadAndPost(
                        imageUris = images,
                        caption = captionText,
                        context = context,
                        navController = navController,
                        latitude = selectedLatitude,
                        longitude = selectedLongitude,
                        onComplete = { success ->
                            isUploading = false
                            if (!success) {
                                Toast.makeText(
                                    context,
                                    "Upload failed. Try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                } else if (isUploading) {
                    Toast.makeText(context, "Already uploading...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Select at least one image.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            enabled = !isUploading
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("🚀 Share")
            }
        }

    }
}

fun uploadAndPost(
    imageUris: List<Uri?>,
    caption: String,
    context: Context,
    navController: NavController?,
    latitude: Double,
    longitude: Double,
    onComplete: (Boolean) -> Unit
) {
    val storage = Firebase.storage
    val auth = Firebase.auth
    val db = Firebase.firestore
    val userId = auth.currentUser?.uid ?: return
    val username = auth.currentUser?.displayName ?: "Anonymous"
    val uploadedUrls = mutableListOf<String>()

    var completed = 0
    imageUris.forEachIndexed { index, uri ->
        if (uri == null) return@forEachIndexed

        val ref =
            storage.reference.child("images/${userId}_${System.currentTimeMillis()}_$index.jpg")
        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                uploadedUrls.add(downloadUrl.toString())
                completed++
                if (completed == imageUris.size) {
                    val post = hashMapOf(
                        "name" to username,
                        "userId" to userId,
                        "caption" to caption,
                        "postImages" to uploadedUrls,
                        "timePosted" to Timestamp.now(),
                        "likes" to 0,
                        "tags" to emptyList<String>(),
                        "latitude" to latitude,
                        "longitude" to longitude
                    )

                    db.collection("users").document(userId).collection("posts")
                        .add(post)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Post Added", Toast.LENGTH_SHORT).show()
                            navController?.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                                launchSingleTop = true
                            }
                            onComplete(true)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Post Failed", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Image Upload Failed", Toast.LENGTH_SHORT).show()
        }
    }
}
