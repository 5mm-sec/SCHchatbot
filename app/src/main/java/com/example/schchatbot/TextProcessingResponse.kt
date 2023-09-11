package com.example.schchatbot

data class TextProcessingResponse(
    val id: Int,
    val text: String,
    val processed_text: String
)