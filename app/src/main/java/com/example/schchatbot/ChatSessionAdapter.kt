package com.example.schchatbot

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatSessionAdapter(private val chatSessionIds: List<Long>) : RecyclerView.Adapter<ChatSessionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sessionId = chatSessionIds[position]
        val dbHelper = ChatDatabaseHelper(holder.itemView.context)
        val session = dbHelper.getChatSession(sessionId) // 해당 세션을 가져옵니다.
        if (session != null) {
            holder.bind(session)
        }
    }

    override fun getItemCount(): Int {
        return chatSessionIds.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //private val sessionIdTextView: TextView = itemView.findViewById(R.id.sessionIdTextView)
        private val firstQuestionTextView: TextView = itemView.findViewById(R.id.RecordChat)

        fun bind(session: ChatSession) {
            // 채팅 세션 ID와 처음 질문을 TextView에 설정
            //sessionIdTextView.text = "채팅 세션 ID: ${session.sessionId}"
            firstQuestionTextView.text = "처음 질문: ${session.firstQuestion}"

            // 채팅 세션을 클릭했을 때 실행할 동작을 여기에 추가
            itemView.setOnClickListener {
                // ChatActivity로 이동하고 채팅 세션 ID를 전달
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("session_id", session.sessionId) // 세션 ID를 전달
                Log.d("전달한 세션 ID", session.sessionId.toString())
                itemView.context.startActivity(intent)
            }
        }
    }
}