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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.sendit.helpers.ExpandableText
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
                                text = "0",
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
                                text = "0",
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
                                text = "0",
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
                Text(text = "$fName $lName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(color = MaterialTheme.colorScheme.primary))
                ExpandableText(userBio)
            }
        }
    }
}
