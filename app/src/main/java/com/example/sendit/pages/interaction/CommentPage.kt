package com.example.sendit.pages.interaction

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sendit.data.CommentData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@Composable
fun CommentPage(
    modifier: Modifier = Modifier,
    userId: String,
    postId: String
) {
    // Firestore
    val db = Firebase.firestore

    // Comments as CommentData Object
    var comments by remember { mutableStateOf(emptyList<CommentData>()) }
    val commentsListener = remember { mutableStateOf<ListenerRegistration?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var commentCount by remember { mutableStateOf(0) }
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(userId, postId) {
        commentsListener.value?.remove() // Remove any existing listener

        val listener = db.collection("users")
            .document(userId)
            .collection("posts")
            .document(postId)
            .collection("comments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CommentPage", "Error listening for comment updates", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    commentCount = snapshot.size()

                    val unsortedComments = snapshot.documents.mapNotNull { document ->
                        val commentId = document.id
                        val postId = document.getString("postId") ?: "No Post ID"
                        val userId = document.getString("userId") ?: "No User ID"
                        val userName = document.getString("name") ?: "No Name"
                        val userImage = document.getString("image") ?: ""
                        val commentText = document.getString("caption") ?: "No caption"
                        val timeStamp = document.getTimestamp("timePosted")

                        // Add this debug logging
                        Log.d(
                            "CommentDebug",
                            "Found comment: ID=$commentId, Text=$commentText, By=$userName"
                        )

                        CommentData(
                            commentId = commentId,
                            userId = userId,
                            userName = userName,
                            userImage = userImage,
                            commentText = commentText,
                            timeStamp = timeStamp,
                            postId = postId
                        )
                    }

                    comments = unsortedComments.sortedByDescending { it.timeStamp }
                    isLoading = false

                    Log.d("Comments", "Comments loaded successfully: ${comments.size}")
                    for (comment in comments) {
                        Log.d("Comments", "Comment: ${comment.commentText} by ${comment.userName}")
                    }
                }
            }
        commentsListener.value = listener
    }

    LaunchedEffect(userId, postId) {
        val listener = db.collection("users")
            .document(userId)
            .collection("posts")
            .document(postId)
            .collection("comments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CommentPage", "Error listening for comment updates", error)
                    return@addSnapshotListener
                }

            }
        commentsListener.value = listener
    }

    DisposableEffect(key1 = postId) {
        onDispose {
            commentsListener.value?.remove()
        }
    }

    // Comment Page UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (comments.isEmpty()) {
            Text(text = "No comments found", style = MaterialTheme.typography.bodyLarge)
        } else {
            for (comment in comments) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = comment.userName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = comment.commentText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = "Comment ID: ${comment.commentId}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth(),
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Add a comment") },
                singleLine = false
            )

            IconButton(onClick = {
                if (commentText.isNotBlank()) {
                    addComment(userId, postId, commentText)
                    commentText = ""
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Add Comment"
                )
            }
        }
    }
}

// Function to add comment to a post
fun addComment(postBy: String, postId: String, commentText: String) {
    val currentUserId = Firebase.auth.currentUser?.uid
    val userName = Firebase.auth.currentUser?.displayName

    val commentsRef = Firebase.firestore.collection("users")
        .document(postBy)
        .collection("posts")
        .document(postId)
        .collection("comments")
        .document()

    val commentData = hashMapOf(
        "commentId" to commentsRef.id,
        "userId" to currentUserId,
        "name" to userName,
        "image" to "",
        "caption" to commentText,
        "timePosted" to com.google.firebase.Timestamp.now(),
        "postId" to postId
    )

    commentsRef.set(commentData)
        .addOnSuccessListener {
            Log.d("Comment", "Comment added successfully")
        }
        .addOnFailureListener { e ->
            Log.w("Comment", "Error adding comment", e)
        }
}