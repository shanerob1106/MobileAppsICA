package com.example.sendit.data

import com.google.firebase.Timestamp

data class PostData(
    val postId: String,
    val userName: String,
    val userImage: String,
    val postImage: String,
    val postCaption: String,
    val timeStamp: Timestamp?,
    val likes: Int = 0,
    val comments: Int = 0,
)