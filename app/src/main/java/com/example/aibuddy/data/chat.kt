package com.example.aibuddy.data

import android.graphics.Bitmap

data class chat (
    val prompt : String,
    val bitmap : Bitmap?,
    val isFromUser : Boolean
)