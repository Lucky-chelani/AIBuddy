package com.example.aibuddy

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aibuddy.data.ChatData
import com.example.aibuddy.data.chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {
    private val _chatState =  MutableStateFlow(ChatState())
    val chatstate  = _chatState.asStateFlow()

    fun onEvent(event: ChatUiEvent){
        when (event){
            is ChatUiEvent.SendPrompt -> {
                if (event.Prompt.isNotEmpty()){
                    addPrompt(event.Prompt , event.bitmap)
                    if (event.bitmap != null){
                        getResponseWithImage(event.Prompt , event.bitmap)

                    } else {
                        getResponse(event.Prompt)

                    }
                }

            }
            is ChatUiEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }
    }

    private fun addPrompt(
        prompt: String ,
        bitmap: Bitmap?
    ){
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0 , chat(prompt, bitmap,true))
                },
                prompt = "",
                bitmap = null
            )
        }
    }
    private fun getResponse(prompt: String){
        viewModelScope.launch {
            val chat = ChatData.getResponse(prompt)
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0 , chat)
                    }
                )
            }
        }
    }
    private fun getResponseWithImage(prompt: String , bitmap: Bitmap){
        viewModelScope.launch {
            val chat = ChatData.getResponseWithImage(prompt , bitmap)
            _chatState.update {
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0 , chat)
                    }
                )
            }
        }
    }
}