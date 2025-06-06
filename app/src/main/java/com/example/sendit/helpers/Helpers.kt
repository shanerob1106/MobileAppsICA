package com.example.sendit.helpers

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil3.compose.AsyncImage
import com.example.sendit.data.PostData
import com.example.sendit.navigation.Screen
import com.example.sendit.pages.activity.RouteType
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// ExpandableText composable
@Composable
fun ExpandableText(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 14.sp) {
    var showMore by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .animateContentSize(animationSpec = tween(100))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { showMore = !showMore }
    ) {
        if (showMore) {
            Text(text = text, fontSize = fontSize)
        } else {
            Text(
                text = text,
                fontSize = fontSize,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Simple Card to display a post
@Composable
fun PostItem(
    post: PostData,
    navController: NavController,
    isCurrentUserPost: Boolean,
    onDeleteClick: () -> Unit
) {

    // Snap image to middle of screen
    val lazyListState = rememberLazyListState()
    val snapBehaviour = rememberSnapFlingBehavior(lazyListState = lazyListState)

    // Track loading state for images
    val imageLoadingStates = remember {
        post.postImages.map { mutableStateOf(true) }
    }

    // Card Element
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        // Main Column
        Column(modifier = Modifier.padding(10.dp)) {

            // User Info Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // User Profile Image or Placeholder
                if (post.userImage.isNotEmpty()) {
                    // Real user image
                    AsyncImage(
                        model = post.userImage,
                        contentDescription = "User Image",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                            .clip(shape = MaterialTheme.shapes.small),
                    )
                } else {
                    // Placeholder with first character of username
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                // Generate consistent color based on username
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.userName.firstOrNull()?.toString() ?: "?",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))

                // Username
                Column {
                    Text(
                        text = post.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                if (isCurrentUserPost) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Post",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Post Images
            LazyRow(
                state = lazyListState,
                flingBehavior = snapBehaviour,
                contentPadding = PaddingValues(horizontal = 0.dp), // No manual edge padding
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(post.postImages.size) { index ->
                    val imageUrl = post.postImages[index]
                    val isLoading = imageLoadingStates[index]
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Post Image ${index + 1}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop,
                            onLoading = { isLoading.value = true },
                            onSuccess = { isLoading.value = false },
                            onError = { isLoading.value = false }
                        )

                        // Show loading indicator when image is loading
                        if (isLoading.value) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Post Caption
            Row {
                ExpandableText(text = post.postCaption)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // User interactions row
            Row {
                // Like button

                LikeButton(post)

                CommentButton(post, navController)
            }

            // Date post was made
            Text(
                text = (post.timeStamp?.toDate()?.let {
                    java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        .format(it)
                }).toString(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun addLike(
    postData: PostData
) {
    // Current User ID
    val currentUserId = Firebase.auth.currentUser?.uid

    // Map of like data
    val likeData = hashMapOf(
        "userId" to currentUserId,
        "timestamp" to com.google.firebase.Timestamp.now()
    )

    // Firestore Collection
    Firebase.firestore.collection("users")
        .document(postData.userId)
        .collection("posts")
        .document(postData.postId)
        .collection("likes")
        .document(currentUserId.toString())
        .set(likeData)
}

fun removeLike(
    postData: PostData
) {
    // Current User ID
    val currentUserId = Firebase.auth.currentUser?.uid

    // Firestore Collection
    Firebase.firestore.collection("users")
        .document(postData.userId)
        .collection("posts")
        .document(postData.postId)
        .collection("likes")
        .document(currentUserId.toString())
        .delete()
}

@Composable
fun LikeButton(post: PostData) {
    val db = Firebase.firestore
    var isLiked by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var likeCount by remember { mutableIntStateOf(0) }

    // Setup Like Button Listener
    LaunchedEffect(isLiked) {
        db.collection("users")
            .document(post.userId)
            .collection("posts")
            .document(post.postId)
            .collection("likes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("LikeButton", "Error listening for likes", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d("LikeButton", "Likes: ${snapshot.size()}")
                    isLiked = snapshot.any { it.id == post.userId }
                    isProcessing = false

                    likeCount = snapshot.size()
                }
            }
    }

    Row {
        // Like Button
        IconButton(
            onClick = {
                if (!isProcessing) {
                    isProcessing = true
                    if (isLiked) {
                        removeLike(post)
                    } else {
                        addLike(post)
                    }
                }
            },
            enabled = !isProcessing
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                if (isLiked) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Post has been liked",
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Post has not been liked",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Text(
            text = likeCount.toString(),
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }
}

@Composable
fun CommentButton(post: PostData, navController: NavController) {

    var commentsCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(post) {
        Firebase.firestore.collection("users")
            .document(post.userId)
            .collection("posts")
            .document(post.postId)
            .collection("comments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CommentButton", "Error listening for comments", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d("CommentButton", "Comments: ${snapshot.size()}")
                    commentsCount = snapshot.size()
                }
            }
    }

    Row {
        // Comments Button
        IconButton(onClick = {
            navController.navigate(Screen.Comments.route + "/${post.userId}/${post.postId}") {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = false
            }
        }) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Comment Button",
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = commentsCount.toString(),
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }
}

fun uploadActivity(
    routeName: String,
    routeGrade: String,
    routeType: RouteType,
    routeTries: String,
    isFlashed: Boolean,
    maxAltitude: Float,
    activityTime: Long,
    longitude: Double,
    latitude: Double,
    timestamp: Timestamp,
    navController: NavController
) {
    val userId = Firebase.auth.currentUser?.uid ?: return
    val db = Firebase.firestore

    val activity = hashMapOf(
        "userId" to userId,
        "routeName" to routeName,
        "routeGrade" to routeGrade,
        "routeTries" to routeTries,
        "maxAltitude" to maxAltitude,
        "isFlashed" to isFlashed,
        "activityTime" to activityTime,
        "longitude" to longitude,
        "latitude" to latitude,
        "routeWeather" to "Sunny",
        "timestamp" to timestamp
    )

    db.collection("users")
        .document(userId)
        .collection("activities")
        .document(routeType.name)
        .collection("sessions")
        .add(activity)
        .addOnSuccessListener {
            navController.navigate(Screen.Activities.route) {
                popUpTo(Screen.Activities.route) { inclusive = true }
                launchSingleTop = true
            }

            Log.d("Activity Upload: PASS", "Activity Uploaded Successfully")

        }
        .addOnFailureListener { exception ->
            Log.w("Activity Upload: FAIL", "Activity Upload Failed", exception)
            // Show error message to user
            Toast.makeText(
                navController.context,
                "Failed to upload activity: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}