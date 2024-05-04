package com.example.aibuddy.data


import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData {
    val api_key = ""

    suspend fun getResponse(prompt: String): chat{
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro", apiKey = api_key

        )
        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            return chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )


        } catch (e: Exception) {
            return chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )

        }

    }




    suspend fun getResponseWithImage(prompt : String , bitmap: Bitmap): chat {
        val generativeModel =  GenerativeModel(
            modelName = "gemini-pro-vision" , apiKey = api_key
        )
        try {
            val inputContent = content {
                image(bitmap)
                text(prompt)
            }
            val response  =  withContext(Dispatchers.IO){
                generativeModel.generateContent(inputContent)
            }
            return chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )


        } catch (e : Exception){
            return chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )

        }

    }
}
