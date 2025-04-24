package com.example.sendit.pages.activity

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.sendit.data.ActivityData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ActivityViewModel {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val _activities = mutableStateOf<List<ActivityData>>(emptyList())
    val activities: State<List<ActivityData>> = _activities

    fun loadActivities(routeType: RouteType) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("activities")
            .document(routeType.name.uppercase())
            .collection("sessions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ActivityViewModel", "Error loading activities", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val sortedActivities = snapshot.documents.mapNotNull { doc ->
                        val activityId = doc.id
                        val routeName = doc.getString("routeName") ?: "No Route Name"
                        val date = doc.getTimestamp("timestamp")
                        val grade = doc.getString("routeGrade") ?: "No Grade"
                        val time = doc.getLong("activityTime") ?: 0L
                        val maxAltitude = doc.getDouble("maxAltitude")?.toFloat() ?: 0f
                        val tries = doc.getString("routeTries") ?: "No Tries"
                        val isFlashed = doc.getBoolean("isFlashed") ?: false

                        ActivityData(
                            activityId = activityId,
                            routeName = routeName,
                            routeGrade = grade,
                            routeTries = tries,
                            isFlashed = isFlashed,
                            timeStamp = date,
                            activityTime = time,
                            maxAltitude = maxAltitude,
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                        )
                    }.sortedByDescending { it.timeStamp }

                    _activities.value = sortedActivities
                }
            }
    }

    fun deleteActivity(userId: String, activityId: String, routeType: RouteType) {
        val db = Firebase.firestore

        db.collection("users")
            .document(userId)
            .collection("activities")
            .document(routeType.name)
            .collection("sessions")
            .document(activityId)
            .delete()
    }
}
