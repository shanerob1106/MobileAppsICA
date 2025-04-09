package com.example.sendit.pages.account

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil3.compose.AsyncImage
import com.example.sendit.data.PostData
import com.example.sendit.helpers.ExpandableText
import com.example.sendit.helpers.PostCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    profileUserId: String? = null
) {
    // Firestore
    val db = Firebase.firestore
    val auth = Firebase.auth
    val userId = profileUserId ?: auth.currentUser?.uid
    val currentUserId = auth.currentUser?.uid

    // Mutable states to store Firestore data
    var userName by remember { mutableStateOf("") }
    var userImage by remember { mutableStateOf("") }
    var fName by remember { mutableStateOf("") }
    var lName by remember { mutableStateOf("") }
    var userBio by remember { mutableStateOf("") }
    var postCount by remember { mutableIntStateOf(0) }
    var followersCount by remember { mutableIntStateOf(0) }
    var followingCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // List of Posts as PostData Object
    var posts by remember { mutableStateOf(emptyList<PostData>()) }

    // Listeners for real-time updates
    val userListener = remember { mutableStateOf<ListenerRegistration?>(null) }
    val postsListener = remember { mutableStateOf<ListenerRegistration?>(null) }

    // Function to load posts
    fun loadPosts() {
        postsListener.value?.remove()

        userId?.let { uid ->
            val listener = db.collection("users").document(uid).collection("posts")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ProfilePage", "Error listening for post updates", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        postCount = snapshot.size()

                        val unsortedPosts = snapshot.documents.mapNotNull { document ->
                            val postId = document.id
                            val postImages =
                                document.get("postImages") as? List<String> ?: emptyList()
                            val postCaption = document.getString("caption") ?: "No caption"
                            val timeStamp = document.getTimestamp("timePosted")
                            val userName = document.getString("name") ?: "No Name"

                            PostData(
                                postId = postId,
                                userName = userName,
                                userImage = userImage,
                                postImages = postImages,
                                postCaption = postCaption,
                                timeStamp = timeStamp,
                                userId = userId
                            )
                        }

                        posts = unsortedPosts.sortedByDescending { it.timeStamp }
                        isLoading = false
                    }
                }

            postsListener.value = listener
        }
    }

    // Set up real-time listeners for user data and posts
    LaunchedEffect(userId) {
        if (userId != null) {
            // Listen for user profile changes
            val listener = db.collection("users").document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ProfilePage", "Error listening for user updates", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        userName = snapshot.getString("username") ?: "No Name Found"
                        userImage = snapshot.getString("image") ?: ""
                        userBio = snapshot.getString("bio") ?: "No Bio Found"
                        fName = snapshot.getString("firstName") ?: "No First Name Found"
                        lName = snapshot.getString("lastName") ?: "No Last Name Found"

                        // Get length of following and followers array
                        val following = snapshot.get("following") as? List<*>
                        val followers = snapshot.get("followers") as? List<*>

                        followingCount = following?.size ?: 0
                        followersCount = followers?.size ?: 0

                        // Load posts after user data is loaded to ensure we have the user image
                        loadPosts()
                    }
                }

            userListener.value = listener
        }
    }

    // Clean up listeners when leaving the screen
    DisposableEffect(key1 = userId) {
        onDispose {
            userListener.value?.remove()
            postsListener.value?.remove()
        }
    }

    // Profile Page UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Card to display profile page
        Card(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                ) {
                    if (userImage.isEmpty()) {
                        // Placeholder with first character of username
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .background(
                                    // Generate consistent color based on username
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.firstOrNull()?.toString() ?: "?",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                    } else {
                        // Profile Image
                        AsyncImage(
                            model = userImage,
                            contentDescription = "User Profile Image",
                            modifier = Modifier
                                .size(150.dp)
                                .padding(5.dp)
                                .clip(shape = MaterialTheme.shapes.extraLarge)
                        )
                    }

                    Column {
                        // Username
                        Text(
                            text = userName,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = TextStyle(color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        // Stats Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Post Count
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = postCount.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = TextStyle(color = MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = "Posts",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Followers Count
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = followersCount.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = TextStyle(color = MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = "Followers",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Following Count
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = followingCount.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = TextStyle(color = MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = "Following",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // More Info Column E.g., Real name and BIO
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "$fName $lName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary)
                )
                ExpandableText(userBio)

                // Follow Button or Sign Out Button based on whether this is the current user's profile
                if (profileUserId == currentUserId) {
                    Button(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Sign Out")
                    }
                } else if (profileUserId != null && currentUserId != null) {
                    FollowButton(profileUserId = profileUserId, currentUserId = currentUserId)
                }
            }
        }

        // Loading indicator or posts
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        } else if (posts.isEmpty()) {
            Text(
                text = "No posts yet",
                modifier = Modifier.padding(16.dp),
                style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
            )
        } else {
            for (post in posts) {
                PostCard(post = post, navController = navController)
            }
        }
    }
}

@Composable
fun FollowButton(profileUserId: String, currentUserId: String) {
    val db = Firebase.firestore
    var isFollowing by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // Set up real-time listener for following status
    LaunchedEffect(profileUserId, currentUserId) {
        if (profileUserId != currentUserId) {
            val listener = db.collection("users").document(profileUserId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FollowButton", "Error listening for follow updates", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val followers = snapshot.get("followers") as? List<*>
                        isFollowing = followers?.contains(currentUserId) == true
                        isProcessing = false  // Reset processing state when we get updated data
                    }
                }
        }
    }

    Button(
        onClick = {
            if (!isProcessing) {  // Prevent multiple clicks while processing
                isProcessing = true
                if (isFollowing) {
                    Unfollow(profileUserId, currentUserId)
                } else {
                    Follow(profileUserId, currentUserId)
                }
            }
        },
        enabled = !isProcessing,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        if (isProcessing) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(text = if (isFollowing) "Unfollow" else "Follow")
        }
    }
}

// Follow function
fun Follow(profileUserId: String, currentUserId: String) {
    val db = Firebase.firestore
    if (profileUserId != currentUserId) {
        db.collection("users").document(profileUserId).update(
            "followers", FieldValue.arrayUnion(currentUserId)
        )
        db.collection("users").document(currentUserId).update(
            "following", FieldValue.arrayUnion(profileUserId)
        )
    }
}

// Unfollow function
fun Unfollow(profileUserId: String, currentUserId: String) {
    val db = Firebase.firestore
    if (profileUserId != currentUserId) {
        db.collection("users").document(profileUserId).update(
            "followers", FieldValue.arrayRemove(currentUserId)
        )
        db.collection("users").document(currentUserId).update(
            "following", FieldValue.arrayRemove(profileUserId)
        )
    }
}