package com.example.sendit.data

import com.google.firebase.Timestamp

data class CommentData(
    val commentId: String,
    val userId: String,
    val userName: String,
    val userImage: String,
    val postId: String,
    val commentText: String,
    val timeStamp: Timestamp?
)