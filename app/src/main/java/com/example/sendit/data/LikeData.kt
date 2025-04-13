package com.example.sendit.data

import com.google.firebase.Timestamp

data class LikeData(
    val userId: String,
    val timeStamp: Timestamp?
)