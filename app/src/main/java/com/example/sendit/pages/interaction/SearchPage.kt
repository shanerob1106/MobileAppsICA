package com.example.sendit.pages.interaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun SearchPage(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<String>()) }

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
                        .clickable{/*Todo: Function to load specific user profile*/}
                        .fillMaxWidth()
                        .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){

                        // User Profile Image
                        // Todo: Replace with actual user profile image
                        AsyncImage(
                            model = "https://picsum.photos/200",
                            contentDescription = "Post Image",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.clip(shape = MaterialTheme.shapes.large)
                        )

                        // Username
                        Text(text = result,
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
// Todo: Retrieve user data from Firestore not just "Username"
fun searchUsers(searchText: String, onResult: (List<String>) -> Unit) {
    val db = Firebase.firestore
    db.collection("users").get()
        .addOnSuccessListener { result ->
            val searchResults = result.documents.mapNotNull { document ->
                document.getString("username")
            }.filter { it.contains(searchText, ignoreCase = true) }

            onResult(searchResults)
        }
        .addOnFailureListener { exception ->
            println("Error getting documents: $exception")
            onResult(emptyList())
        }
}