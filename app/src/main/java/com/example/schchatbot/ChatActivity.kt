package com.example.schchatbot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log // 로그를 사용하기 위한 임포트
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schchatbot.databinding.ActivityChatBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    val retrofit = RetrofitInstance.retrofit
    val apiService = retrofit.create(ApiService::class.java)

    private lateinit var binding: ActivityChatBinding

    lateinit var chatAdapter: ChatAdapter
    val messages = mutableListOf<ChatMessage>() // messages 리스트를 초기화

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

                                val serverResponseMessage = ChatMessage(processedText, false)
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

                val userMessage = ChatMessage(chat_send_edittext, true)
                messages.add(userMessage)

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
}


