package com.example.sendit.data

import com.google.firebase.Timestamp

data class ActivityData(
    // Activity ID
    val activityId: String,

    // Firebase Timestamp
    val timeStamp: Timestamp?,

    // Longitude and Latitude
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    // Route Info
    val routeType: String = "",
    val routeName: String = "",
    val routeGrade: String = "",

    // Activity Info
    val activityTime: Long = 0L,
    val maxAltitude: Float = 0f
)
