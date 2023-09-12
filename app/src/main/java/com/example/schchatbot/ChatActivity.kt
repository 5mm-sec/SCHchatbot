package com.example.schchatbot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schchatbot.databinding.ActivityChatBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    val retrofit = RetrofitInstance.retrofit
    val apiService = retrofit.create(ApiService::class.java)
    private val databaseHelper = ChatDatabaseHelper(this)
    private lateinit var binding: ActivityChatBinding
    private var currentChatSessionId: String = ""
    lateinit var chatAdapter: ChatAdapter
    val messages = mutableListOf<ChatMessage>() // messages 리스트를 초기화
    private var currentSessionId: String = ""
    private var previousSessionId: Long = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatAdapter = ChatAdapter(messages)


        // 채팅 세션 ID를 전달받습니다.
        val sessionId = intent.getLongExtra("session_id", -1L)
        if (sessionId != -1L) {
            // 전달받은 세션 ID로 채팅 내용을 가져와서 리사이클러뷰에 추가합니다.
            val dbHelper = ChatDatabaseHelper(this)
            val chatMessages = dbHelper.getChatMessagesForSession(sessionId)

            // chatMessages를 RecyclerView의 어댑터에 추가합니다.
            chatAdapter.addMessages(chatMessages)
        }
        previousSessionId = sessionId

        currentSessionId = generateChatSessionId()
        // 화면이 처음 열릴 때 새로운 채팅 세션 식별자 생성
//        currentChatSessionId = generateChatSessionId()
        val chat_send_button = binding.chatSendButton
        chat_send_button.setOnClickListener {
            val chat_send_edittext = binding.chatSendEdittext.text.toString()

            if (chat_send_edittext.isNotEmpty()) {

                val request = TextProcessingRequest(text = chat_send_edittext)
                Log.d("요청한 질문", chat_send_edittext)

                val call = apiService.processText(request)
                call.enqueue(object : Callback<TextProcessingResponse> {
                    override fun onResponse(call: Call<TextProcessingResponse>, response: Response<TextProcessingResponse>) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                val id = data.id
                                val processedText = data.processed_text

                                Log.d("ChatActivity", "응답 ID: $id")
                                Log.d("ChatActivity", "챗봇 서버의 응답: $processedText")


                                // 서버 응답 메시지에 대한 코드 수정
                                val serverResponseMessage = ChatMessage(processedText, false, databaseHelper.getCurrentTimestamp())
                                messages.add(serverResponseMessage)

                                // RecyclerView 갱신
                                chatAdapter.notifyDataSetChanged()
                            }
                        } else {
                            Log.e("ChatActivity", "API 통신 실패: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<TextProcessingResponse>, t: Throwable) {
                        Log.e("ChatActivity", "API 통신 실패: ${t.message}")
                    }
                })


                // 사용자 메시지에 대한 코드 수정
                val userMessage = ChatMessage(chat_send_edittext, true, databaseHelper.getCurrentTimestamp())
                messages.add(userMessage)

                // 데이터베이스에 사용자 메시지 삽입
                databaseHelper.insertChatMessage(userMessage)

                // 로그에 사용자 메시지 저장 확인 메시지 출력
                Log.d("ChatActivity", "사용자 메시지 저장 확인: ${userMessage.messageText}")

                // RecyclerView 갱신
                chatAdapter.notifyDataSetChanged()




                // RecyclerView 갱신
                chatAdapter.notifyDataSetChanged()

                binding.chatSendEdittext.text.clear()
            } else {
                Log.d("ChatActivity", "사용자가 텍스트를 입력하지 않았습니다.")
            }
        }

        val recyclerViewChat = findViewById<RecyclerView>(R.id.recyclerViewChat)
        val layoutManager = LinearLayoutManager(this)
        recyclerViewChat.layoutManager = layoutManager

        // ChatAdapter 초기화 및 RecyclerView에 설정
        chatAdapter = ChatAdapter(messages) // messages 리스트를 ChatAdapter 생성자에 전달
        recyclerViewChat.adapter = chatAdapter
    }
    override fun onPause() {
        super.onPause()
        // 화면을 벗어났을 때 채팅 세션을 데이터베이스에 저장
        saveChatSessionToDatabase()

        // 이전 채팅 세션을 삭제합니다.
        deletePreviousChatSession()
    }



    private fun deletePreviousChatSession() {
        if (previousSessionId != -1L) {
            // 이전 채팅 세션을 삭제합니다.
            val dbHelper = ChatDatabaseHelper(this)

            // previousSessionId와 다른 세션을 삭제하는 로직을 구현합니다.
            val sessionIdsToDelete = dbHelper.getAllChatSessionIds().filter { it != previousSessionId }

            for (sessionId in sessionIdsToDelete) {
                dbHelper.deleteChatSession(sessionId.toString())
            }
        }
    }

    private fun generateChatSessionId(): String {
        // 여기에서 고유한 채팅 세션 식별자를 생성하는 코드 작성
        return "Session_${System.currentTimeMillis()}"
    }

    private fun saveChatSessionToDatabase() {
        // 채팅 세션 시작
        databaseHelper.startChatSession()

        if (messages.isNotEmpty()) {
            // messages 리스트에 채팅 메시지가 저장되어 있다고 가정합니다.
            for (message in messages) {
                // 데이터베이스에 채팅 메시지 삽입 및 현재 채팅 세션 식별자 함께 저장
                databaseHelper.insertChatMessage(message)
            }
        }

        // 채팅 세션 종료
        databaseHelper.endChatSession()
    }
}


