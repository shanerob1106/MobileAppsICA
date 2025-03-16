package com.example.sendit.data

data class ProfileData(
    val userId: String,
    val userEmail: String,
    val userName: String,
    val userBio: String,
    val userImage: String,
    val firstName: String,
    val lastName: String,
    val followers: List<String>,
    val following: List<String>
)