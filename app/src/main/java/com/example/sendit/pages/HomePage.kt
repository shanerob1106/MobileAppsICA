package com.example.sendit.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.sendit.data.PostData
import com.example.sendit.helpers.PostItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    // Firestore
    val db = Firebase.firestore

    var posts by remember { mutableStateOf(emptyList<PostData>()) }

    LaunchedEffect(true) { // Trigger on composable launch
        db.collection("users")
            .get()
            .addOnSuccessListener { usersSnapshot ->
                val allPosts = mutableListOf<PostData>()
                val totalPosts = usersSnapshot.size()
                var processedUsers = 0

                for (userDoc in usersSnapshot) {
                    val userId = userDoc.id

                    db.collection("users")
                        .document(userId)
                        .collection("posts")
                        .get()
                        .addOnSuccessListener { postsSnapshot ->
                            val userPosts = postsSnapshot.documents.mapNotNull { document ->
                                val postId = document.id
                                val postImages = document.get("postImages") as? List<String>
                                    ?: emptyList<String>()
                                val postCaption = document.getString("caption") ?: "No caption"
                                val timeStamp = document.getTimestamp("timePosted")
                                val userName = userDoc.getString("username") ?: "Unknown User"

                                PostData(
                                    postId = postId,
                                    userName = userName,
                                    userImage = "",     // No user profile image
                                    postImages = postImages,     // No post image(s)
                                    postCaption = postCaption,
                                    timeStamp = timeStamp,
                                    userId = userId
                                )
                            }

                            allPosts.addAll(userPosts)

                            processedUsers++
                            if (processedUsers == totalPosts) {
                                posts = allPosts.sortedByDescending { it.timeStamp }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("ProfilePage", "Error getting posts for user $userId", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ProfilePage", "Error fetching users", exception)
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (post in posts) {
            PostItem(
                post = post,
                navController = navController,
                isCurrentUserPost = false,
                onDeleteClick = { /*Leave Empty*/ }
            )
        }
    }
}
