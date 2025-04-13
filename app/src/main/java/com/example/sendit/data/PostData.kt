package com.example.sendit.data

import com.google.firebase.Timestamp

data class PostData(
    val postId: String,
    val userId: String,
    val userName: String,
    val userImage: String,
    val postImages: List<String>,
    val postCaption: String,
    val timeStamp: Timestamp?,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)