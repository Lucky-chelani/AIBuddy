package com.example.aibuddy

import android.graphics.Bitmap

sealed class ChatUiEvent {
    data class UpdatePrompt(val newPrompt: String): ChatUiEvent()
    data class SendPrompt(val Prompt: String, val bitmap: Bitmap?): ChatUiEvent()
}

