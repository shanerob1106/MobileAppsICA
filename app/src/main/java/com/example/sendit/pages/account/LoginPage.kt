package com.example.sendit.pages.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun LoginPage(onLoginSuccess: () -> Unit) {
    // Base
    var emailString by remember { mutableStateOf("") }
    var passwordString by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    var confirmPasswordString by remember { mutableStateOf("") }

    // New profile setup fields
    var firstNameString by remember { mutableStateOf("") }
    var lastNameString by remember { mutableStateOf("") }
    var bioString by remember { mutableStateOf("") }
    var usernameString by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    // Validation function
    fun validateRegistrationFields(): Boolean {
        return when {
            emailString.isEmpty() -> {
                Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }

            passwordString.isEmpty() -> {
                Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }

            passwordString != confirmPasswordString -> {
                Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                false
            }

            isRegistering && (firstNameString.isEmpty() || lastNameString.isEmpty() || usernameString.isEmpty()) -> {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT)
                    .show()
                false
            }

            else -> true
        }
    }

    // Main Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://logo.svgcdn.com/d/android-original.png",
            contentDescription = "App Logo",
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = if (isRegistering) "Create an Account" else "Welcome Back",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = emailString,
            onValueChange = { emailString = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = passwordString,
            onValueChange = { passwordString = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (isRegistering) {
            Spacer(modifier = Modifier.height(8.dp))

            // Additional profile setup fields
            OutlinedTextField(
                value = firstNameString,
                onValueChange = { firstNameString = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastNameString,
                onValueChange = { lastNameString = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = usernameString,
                onValueChange = { usernameString = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = bioString,
                onValueChange = { bioString = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPasswordString,
                onValueChange = { confirmPasswordString = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isRegistering) {
                    // Registration logic with profile creation
                    if (validateRegistrationFields()) {
                        auth.createUserWithEmailAndPassword(emailString, passwordString)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Get the current user's UID
                                    val userId = auth.currentUser?.uid

                                    if (userId != null) {
                                        // Create user profile in Firestore
                                        val userProfile = hashMapOf(
                                            "email" to emailString,
                                            "firstName" to firstNameString,
                                            "lastName" to lastNameString,
                                            "username" to usernameString,
                                            "bio" to bioString,
                                            "profilePictureUrl" to "", // Optional: can be updated later
                                            "joinedDate" to com.google.firebase.Timestamp.now()
                                        )

                                        db.collection("users").document(userId)
                                            .set(userProfile)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Account created successfully!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onLoginSuccess()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    context,
                                                    "Profile creation failed: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Registration failed: ${task.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    // Login logic
                    if (emailString.isNotEmpty() && passwordString.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(emailString, passwordString)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT)
                                        .show()
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Login failed: ${task.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "Please enter email and password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isRegistering) "Create Account" else "Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isRegistering) "Already have an account?" else "Don't have an account?",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = {
                    isRegistering = !isRegistering
                    // Clear all fields when switching modes
                    passwordString = ""
                    confirmPasswordString = ""
                    firstNameString = ""
                    lastNameString = ""
                    usernameString = ""
                    bioString = ""
                }
            ) {
                Text(text = if (isRegistering) "Log In" else "Sign Up")
            }
        }
    }
}