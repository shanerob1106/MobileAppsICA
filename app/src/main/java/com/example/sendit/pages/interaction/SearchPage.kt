package com.example.sendit.pages.interaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sendit.data.ProfileData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    // Mutable states to store Firestore data
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<ProfileData>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                singleLine = true
            )

            // Search Button
            Button(
                onClick = {
                    searchUsers(searchQuery) { results ->
                        searchResults = results
                    }
                }
            ) {
                // Default text
                Text(text = "Search")
            }
        }

        if (searchResults.isNotEmpty()) {

            // List of Profile found
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { result ->
                    Row(modifier = Modifier
                        .clickable {
                            navController.navigate("profile/${result.userId}") {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                                restoreState = false
                            }

                        }
                        .fillMaxWidth()
                        .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = result.userName.firstOrNull()?.toString() ?: "?",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        // Username
                        Text(
                            text = result.userName,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// Search function to get user data from Firestore
fun searchUsers(searchText: String, onResult: (List<ProfileData>) -> Unit) {
    val db = Firebase.firestore
    db.collection("users").get()
        .addOnSuccessListener { result ->
            val searchResults = result.documents.mapNotNull { document ->
                val username = document.getString("username")
                if (username?.contains(searchText, ignoreCase = true) == true) {
                    ProfileData(
                        userId = document.id,
                        userEmail = document.getString("email") ?: "",
                        userName = username,
                        userBio = document.getString("bio") ?: "",
                        userImage = document.getString("profilePictureUrl") ?: "",
                        firstName = document.getString("firstName") ?: "",
                        lastName = document.getString("lastName") ?: "",
                        followers = document.get("followers") as List<String>,
                        following = document.get("following") as List<String>,
                        userLongitude = document.getDouble("userLongitude"),
                        userLatitude = document.getDouble("userLatitude")
                    )
                } else {
                    null
                }
            }

            onResult(searchResults)
        }
        .addOnFailureListener { exception ->
            println("Error getting documents: $exception")
            onResult(emptyList())
        }
}