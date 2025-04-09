package com.example.sendit.helpers

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
import androidx.compose.material.icons.automirrored.outlined.Send
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
import androidx.compose.runtime.getValue
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
fun PostCard(post: PostData, navController: NavController) {

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
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
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
                IconButton(onClick = {/*Todo*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like Button",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Like Counter
                Text(
                    text = post.likes.toString(),
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )

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

                // Comments Counter
                Text(
                    text = "0",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )

                // Send Post button
                IconButton(onClick = {/*Todo*/ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Send Button",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Date post was made
            Text(
                text = "Date Posted: " + post.timeStamp?.toDate().toString(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}