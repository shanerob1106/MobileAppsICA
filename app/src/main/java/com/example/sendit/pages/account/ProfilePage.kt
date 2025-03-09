package com.example.sendit.pages.account

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.sendit.data.PostData
import com.example.sendit.helpers.ExpandableText
import com.example.sendit.pages.PostCard
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {

    // Firestore
    val db = Firebase.firestore
    val auth = Firebase.auth
    val userId = auth.currentUser?.uid

    // Mutable states to store Firestore data
    var userName by remember { mutableStateOf("") } // Mutable state to trigger recomposition
    var fName by remember { mutableStateOf("") }
    var lName by remember { mutableStateOf("") }
    var userBio by remember { mutableStateOf("") }
    var postCount by remember { mutableIntStateOf(0) }
    var followersCount by remember { mutableIntStateOf(0) }
    var followingCount by remember { mutableIntStateOf(0) }

    var posts by remember { mutableStateOf(emptyList<PostData>()) }

    // Ensures the Firestore call runs only when the composable launches
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        Log.d("ProfilePage", "DocumentSnapshot data: ${document.data}")
                        userName = document.getString("username") ?: "No Name Found"
                        userBio = document.getString("bio") ?: "No Bio Found"
                        fName = document.getString("firstName") ?: "No First Name Found"
                        lName = document.getString("lastName") ?: "No Last Name Found"

                        // Get length of following and followers array
                        val following = document.get("following") as? List<*>
                        val followers = document.get("followers") as? List<*>

                        if (following != null) {
                            followingCount = following.size
                        }

                        if (followers != null) {
                            followersCount = followers.size
                        }

                        db.collection("users").document(userId).collection("posts")
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                postCount = querySnapshot.size()

                                posts = querySnapshot.documents.mapNotNull { document ->
                                    val postId = document.id  // Document ID as Post ID
                                    val postCaption = document.getString("caption") ?: "No caption"
                                    val timeStamp = document.getTimestamp("timePosted").toString()
                                    val userName = document.getString("name") ?: "No Name"

                                    PostData(
                                        postId = postId,
                                        userName = userName,       // Empty since not needed now
                                        userImage = "",      // Empty since not needed now
                                        postImage = "",      // Empty since not needed now
                                        postCaption = postCaption,
                                        timeStamp = timeStamp
                                    )
                                }

                                Log.d("ProfilePage", "Post: $posts")
                            }
                            .addOnFailureListener { exception ->
                                Log.d("ProfilePage", "Error getting posts: ", exception)
                            }

                    } else {
                        Log.d("ProfilePage", "No such document")
                        userName = "User not found"
                        userBio = "Bio not found"
                        fName = "First Name not found"
                        lName = "Last Name not found"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("ProfilePage", "get failed with ", exception)
                    userName = "Error loading profile"
                    userBio = "Error loading profile"
                    fName = "Error loading profile"
                    lName = "Error loading profile"
                }
        }
    }

    // Profile Page UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
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
                // Username
                Text(
                    text = userName,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                ) {
                    // Profile Image
                    AsyncImage(
                        model = "https://picsum.photos/100",
                        contentDescription = "User Profile Image",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(5.dp)
                    )

                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
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

            // More Info Column E.g., Real name and BIO
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "$fName $lName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary)
                )
                ExpandableText(userBio)
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(posts.size) { index ->
                PostCard(post = posts[index])
            }
        }
    }
}