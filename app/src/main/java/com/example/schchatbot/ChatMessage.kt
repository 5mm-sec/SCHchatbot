package com.example.schchatbot

data class ChatMessage(
    val messageText: String,
    val isUserMessage: Boolean,
    val timestamp: String,
    var chatSessionId: String = "" // 채팅 세션 식별자 추가
)
