package com.example.schchatbot

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatSessionAdapter(private val chatSessionIds: MutableList<Long>) : RecyclerView.Adapter<ChatSessionAdapter.ViewHolder>()  {

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
    // 채팅 세션 삭제 함수
    // 채팅 세션 삭제 함수
    // 채팅 세션 삭제 함수
    // 채팅 세션 삭제 함수
    private fun deleteChatSession(context: Context, position: Int) {
        val chatSessionId = chatSessionIds[position]
        val dbHelper = ChatDatabaseHelper(context)

        // 채팅 세션 삭제
        dbHelper.deleteChatSession(chatSessionId.toString())

        // chatSessionIds 리스트에서 아이템 제거
        chatSessionIds.removeAt(position)

        // RecyclerView 갱신
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //private val sessionIdTextView: TextView = itemView.findViewById(R.id.sessionIdTextView)
        private val firstQuestionTextView: TextView = itemView.findViewById(R.id.RecordChat)
        val trashcanImageView: ImageView = itemView.findViewById(R.id.trashcanImageView)
        init {
            trashcanImageView.setOnClickListener {
                val adapterPosition = adapterPosition // 이 위치에서 adapterPosition을 가져오도록 수정
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // ViewHolder 내부에서 itemView를 이용하여 context를 가져옴
                    val context = itemView.context

                    // 클릭한 아이템의 위치를 가져와 해당 채팅 세션 삭제
                    deleteChatSession(context, adapterPosition)
                }
            }
        }
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