package com.example.schchatbot

import java.util.Date

class ChatSession(
    val sessionId: Long,
    val startTimestamp: Date?, // Date? 타입으로 변경
    val firstQuestion: String
)