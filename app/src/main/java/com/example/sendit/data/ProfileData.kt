package com.example.sendit.data

data class ProfileData(

    // User Data
    val userId: String,
    val userEmail: String,
    val userName: String,
    val userBio: String,
    val userImage: String,
    val firstName: String,
    val lastName: String,

    // Followers/following
    val followers: List<String>,
    val following: List<String>,

    // User location
    val userLongitude: Double?,
    val userLatitude: Double?
)