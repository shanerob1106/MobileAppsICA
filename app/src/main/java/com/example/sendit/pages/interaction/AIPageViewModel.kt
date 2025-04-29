package com.example.sendit.pages.interaction

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

// ViewModel for Selected AI Image
class AIPageViewModel : ViewModel() {
    val selectedImageBitmap = MutableLiveData<Bitmap?>()
}