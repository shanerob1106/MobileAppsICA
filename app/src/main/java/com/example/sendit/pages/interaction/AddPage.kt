package com.example.sendit.pages.interaction

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.example.sendit.navigation.Screen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@Preview
@Composable
fun AddPage(
    navController: NavController? = null,
) {

    val context = LocalContext.current
    var captionText by remember { mutableStateOf("") }
    val result = remember { mutableStateOf<List<Uri?>?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
            result.value = it
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            // Page Title
            Text(
                text = "New Post",
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(color = MaterialTheme.colorScheme.primary),
            )

            // Image browser button
            Button(
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                Text(text = "Select Images")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hide post elements
            if (result.value == null) {
                Text(
                    text = "Select an image",
                    modifier = Modifier.padding(10.dp)
                )
            } else {
                // Show images once selected
                result.value?.let { images ->
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)) {
                        items(images) {
                            //Use Coil to display the selected image
                            val painter = rememberAsyncImagePainter(
                                ImageRequest
                                    .Builder(LocalContext.current)
                                    .data(data = it)
                                    .build()
                            )
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(150.dp, 150.dp)
                                    .padding(5.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = captionText,
                        onValueChange = { captionText = it },
                        label = { Text("Add A Caption...") },
                    )
                    Button(

                        onClick = {
                            val images = result.value ?: emptyList()
                            if (images.isNotEmpty()) {
                                uploadImages(images, context) { imageUrls ->
                                    if (navController != null) {
                                        addContent(captionText, imageUrls, context, navController)
                                    }
                                }
                            } else {
                                Toast.makeText(context, "No images selected", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                    ) {
                        Text(text = "Post")
                    }
                }
            }
        }
    }
}

fun uploadImages(
    imageUris: List<Uri?>,
    context: Context,
    onComplete: (List<String>) -> Unit
) {
    val storage = Firebase.storage
    val auth = Firebase.auth
    val userId = auth.currentUser?.uid ?: return
    val imageUrls = mutableListOf<String>()

    var uploadedCount = 0

    imageUris.forEachIndexed { index, uri ->
        if (uri == null) return@forEachIndexed

        val fileName = "images/${userId}_${System.currentTimeMillis()}_${index}.jpg"
        val storageRef = storage.reference.child(fileName)

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    imageUrls.add(downloadUrl.toString())
                    uploadedCount++
                    if (uploadedCount == imageUris.size) {
                        onComplete(imageUrls)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
            }
    }
}

fun addContent(
    content: String,
    imageUrls: List<String>,
    context: Context,
    navController: NavController
) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val userId = auth.currentUser?.uid
    val username = auth.currentUser?.displayName

    if (userId != null) {
        val post = hashMapOf(
            "name" to username,
            "caption" to content,
            "postImages" to imageUrls,
            "timePosted" to com.google.firebase.Timestamp.now(),
            "likes" to 0,
            "comments" to emptyList<String>(),
            "location" to "Somewhere", /*Todo: Add location data GPS?*/
            "tags" to emptyList<String>()
        )

        db.collection("users").document(userId).collection("posts")
            .add(post)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "Post Added", Toast.LENGTH_SHORT).show()

                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Post Failed", Toast.LENGTH_SHORT).show()
            }
    } else {
        Log.w(TAG, "User not authenticated.")
    }
}


