package com.example.sendit.pages.interaction

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel for Selected AI Image
class AIPageViewModel : ViewModel() {
    val selectedImageBitmap = MutableLiveData<Bitmap?>()
}