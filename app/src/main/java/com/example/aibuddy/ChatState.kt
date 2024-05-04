package com.example.aibuddy

import android.graphics.Bitmap
import com.example.aibuddy.data.chat

data class ChatState(
    val chatList: MutableList<chat> = mutableListOf(),
    val prompt : String = "",
    val bitmap: Bitmap?= null
)
