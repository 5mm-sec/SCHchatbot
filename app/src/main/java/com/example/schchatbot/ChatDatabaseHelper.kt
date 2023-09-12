package com.example.schchatbot

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date


class ChatDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var currentChatSessionId: Long = -1
    private val messages = mutableListOf<ChatMessage>() // 채팅 메시지 리스트 추가
    companion object {
        const val DATABASE_NAME = "chat.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS chat_sessions (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "start_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")"
        )

        // chat_messages 테이블의 정의를 변경하여 session_id 컬럼을 추가합니다.
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS chat_messages (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "session_id INTEGER," +
                    "message_text TEXT," +
                    "is_user_message INTEGER," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(session_id) REFERENCES chat_sessions(_id)" +
                    ")"
        )
    }
    fun startChatSession() {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("start_timestamp", getCurrentTimestamp())
        }
        currentChatSessionId = db.insert("chat_sessions", null, values)
        db.close()

        Log.d("ChatDatabaseHelper", "채팅 세션을 시작했습니다. 세션 ID: $currentChatSessionId")
    }

    fun endChatSession() {
        if (currentChatSessionId != -1L) {
            // 현재 채팅 세션을 종료하고 채팅 내용을 데이터베이스에 저장합니다.
            saveChatMessagesToSession(currentChatSessionId, messages)
            currentChatSessionId = -1

            Log.d("ChatDatabaseHelper", "채팅 세션을 저장하고 종료했습니다.")
        }
    }



    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 데이터베이스 버전 업그레이드를 처리합니다. (필요한 경우)
    }


    fun saveChatMessagesToSession(currentSessionId: Long, messages: List<ChatMessage>) {
        val db = writableDatabase
        for (message in messages) {
            val values = ContentValues().apply {
                put("session_id", currentSessionId)
                put("message_text", message.messageText)
                put("is_user_message", if (message.isUserMessage) 1 else 0)
            }
            db.insert("chat_messages", null, values)
        }
        db.close()
    }



    @SuppressLint("Range")
    fun getChatSession(sessionId: Long): ChatSession? {
        val db = readableDatabase
        var session: ChatSession? = null

        val cursor = db.rawQuery("SELECT * FROM chat_sessions WHERE _id = ?", arrayOf(sessionId.toString()))
        if (cursor.moveToFirst()) {
            val startTimestampStr = cursor.getString(cursor.getColumnIndex("start_timestamp"))

            // String을 Date 객체로 변환
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val startTimestamp = dateFormat.parse(startTimestampStr)

            // 처음 질문을 가져오기
            val firstQuestion = getFirstQuestionForSession(sessionId)

            session = ChatSession(sessionId, startTimestamp, firstQuestion)
        }

        cursor.close()
        db.close()

        return session
    }

    @SuppressLint("Range")
    fun getFirstQuestionForSession(sessionId: Long): String {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT message_text FROM chat_messages WHERE session_id = ? AND is_user_message = 1 ORDER BY timestamp ASC LIMIT 1",
            arrayOf(sessionId.toString())
        )
        var firstQuestion = ""
        if (cursor.moveToNext()) {
            firstQuestion = cursor.getString(cursor.getColumnIndex("message_text"))
        }
        cursor.close()
        db.close()
        return firstQuestion
    }


    fun insertChatMessage(chatMessage: ChatMessage) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("session_id", currentChatSessionId)
            put("message_text", chatMessage.messageText)
            put("is_user_message", if (chatMessage.isUserMessage) 1 else 0)
        }
        db.insert("chat_messages", null, values)
        db.close()
    }
    @SuppressLint("Range")
    fun getChatMessagesForSession(sessionId: Long): List<ChatMessage> {
        val db = readableDatabase
        val messages = mutableListOf<ChatMessage>()

        val cursor = db.rawQuery("SELECT * FROM chat_messages WHERE session_id = ?", arrayOf(sessionId.toString()))
        while (cursor.moveToNext()) {
            val messageText = cursor.getString(cursor.getColumnIndex("message_text"))
            val isUserMessage = cursor.getInt(cursor.getColumnIndex("is_user_message")) == 1
            val timestamp = cursor.getString(cursor.getColumnIndex("timestamp"))
            val chatMessage = ChatMessage(messageText, isUserMessage, timestamp)
            messages.add(chatMessage)
        }

        cursor.close()
        db.close()

        return messages
    }
    @SuppressLint("Range")
    fun getAllChatSessionIds(): List<Long> {
        val db = readableDatabase
        val sessionIds = mutableListOf<Long>()

        val cursor = db.rawQuery("SELECT _id FROM chat_sessions", null)
        while (cursor.moveToNext()) {
            val sessionId = cursor.getLong(cursor.getColumnIndex("_id"))
            sessionIds.add(sessionId)
        }

        cursor.close()
        db.close()

        return sessionIds
    }
    fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }

    fun deleteChatSession(sessionIdToDelete: String) {
        val db = writableDatabase

        // 세션을 삭제하기 전에 해당 세션에 속하는 메시지도 함께 삭제합니다.
        db.delete("chat_messages", "session_id = ?", arrayOf(sessionIdToDelete.toString()))

        // 세션을 삭제합니다.
        db.delete("chat_sessions", "_id = ?", arrayOf(sessionIdToDelete.toString()))

        db.close()
    }


}