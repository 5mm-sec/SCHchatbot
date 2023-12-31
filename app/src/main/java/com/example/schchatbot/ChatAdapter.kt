package com.example.schchatbot
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ChatAdapter(private val messages: MutableList<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResId = if (viewType == USER_MESSAGE_VIEW_TYPE) R.layout.item_user_message else R.layout.item_bot_message
        val itemView = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        Log.d("ChatAdapter", "Binding message: ${message.messageText}")
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }
    fun addMessages(newMessages: List<ChatMessage>) {
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun insertChatMessage(message: ChatMessage) {
        // 데이터베이스에 메시지 삽입하는 코드

        // 로그에 채팅 세션 저장 확인 메시지 출력
        Log.d("ChatDatabaseHelper", "채팅 세션 저장 확인: ${message.messageText}")
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.isUserMessage) {
            USER_MESSAGE_VIEW_TYPE
        } else {
            BOT_MESSAGE_VIEW_TYPE
        }
    }

    // ChatAdapter 코드
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.MessageTextView)

        fun bind(message: ChatMessage) {
            messageTextView.text = message.messageText
        }
    }

    companion object {
        private const val USER_MESSAGE_VIEW_TYPE = 1
        private const val BOT_MESSAGE_VIEW_TYPE = 2
    }
}