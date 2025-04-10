package com.example.sendit.pages.interaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.sendit.data.CommentData
import com.example.sendit.helpers.ExpandableText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
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
    val currentUser = Firebase.auth.currentUser

    // Comments as CommentData Object
    var comments by remember { mutableStateOf(emptyList<CommentData>()) }
    val commentsListener = remember { mutableStateOf<ListenerRegistration?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var commentCount by remember { mutableIntStateOf(0) }
    var commentText by remember { mutableStateOf("") }

    // State for delete confirm
    var showDeleteDialog by remember { mutableStateOf(false) }
    var commentToDelete by remember { mutableStateOf<CommentData?>(null) }

    LaunchedEffect(userId, postId) {
        commentsListener.value?.remove()

        val listener = db.collection("users")
            .document(userId)
            .collection("posts")
            .document(postId)
            .collection("comments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
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

                }
            }
        commentsListener.value = listener
    }

    DisposableEffect(key1 = postId) {
        onDispose {
            commentsListener.value?.remove()
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && commentToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                commentToDelete = null
            },
            title = { Text("Delete Comment") },
            text = { Text("Are you sure you want to delete this comment?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        commentToDelete?.let { deleteComment(it, db, userId, postId) }
                        showDeleteDialog = false
                        commentToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        commentToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Use Scaffold for fixed bottom bar layout
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Comment Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
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

                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            addComment(userId, postId, commentText)
                            commentText = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Add Comment"
                    )
                }
            }
        }
    ) { paddingValues ->
        // Comment List Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            if (comments.isEmpty()) {
                item {
                    Text(
                        text = "No comments found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(comments) { comment ->
                    CommentItem(
                        comment = comment,
                        isCurrentUserComment = comment.userId == currentUser?.uid,
                        onDeleteClick = {
                            commentToDelete = comment
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentData,
    isCurrentUserComment: Boolean,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // User Profile Image
        if (comment.userImage.isNotEmpty()) {
            AsyncImage(
                model = comment.userImage,
                contentDescription = "User Image",
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp)
            )
        } else {
            // User Profile Image Placeholder
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comment.userName.firstOrNull()?.toString() ?: "?",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Username row with delete button for own comments
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Username
                    Text(
                        text = comment.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Delete button - only visible for current user's comments
                    if (isCurrentUserComment) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Comment",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Expandable comment text
                ExpandableText(
                    text = comment.commentText,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }
        }
    }
}

// Function to add comment
fun addComment(
    userId: String,
    postId: String,
    commentText: String
) {
    val currentUserId = Firebase.auth.currentUser?.uid
    val userName = Firebase.auth.currentUser?.displayName

    val commentsRef = Firebase.firestore.collection("users")
        .document(userId)
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
}

// Function to delete a comment
fun deleteComment(
    comment: CommentData,
    db: FirebaseFirestore = Firebase.firestore,
    userId: String,
    postId: String
) {
    db.collection("users")
        .document(userId)
        .collection("posts")
        .document(postId)
        .collection("comments")
        .document(comment.commentId)
        .delete()
}