package com.example.schchatbot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // "main_new_chat" ImageView 찾기
        val mainNewChatImageView = view.findViewById<ImageView>(R.id.main_new_chat)

        // "main_new_chat" ImageView에 클릭 이벤트 설정
        mainNewChatImageView.setOnClickListener {
            // ChatActivity로 이동하는 인텐트 생성
            Log.d("채팅화면으로 이동", "채팅화면으로 이동")
            val intent = Intent(activity, ChatActivity::class.java)
            startActivity(intent)
        }

        // ChatDatabaseHelper를 사용하여 저장된 채팅 세션의 고유 ID 목록을 가져옴
        val dbHelper = ChatDatabaseHelper(requireContext())
        val chatSessionIds = dbHelper.getAllChatSessionIds()

        // 가져온 채팅 세션 ID 목록을 로그에 출력합니다.
        Log.d("HomeFragment", "채팅 세션 ID 목록: $chatSessionIds")



        // RecyclerView에 채팅 세션 목록을 표시하기 위한 어댑터 설정
        val recyclerViewSessions = view.findViewById<RecyclerView>(R.id.recyclerViewRecordChat)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerViewSessions.layoutManager = layoutManager
        val sessionAdapter = ChatSessionAdapter(chatSessionIds as MutableList<Long>)
        recyclerViewSessions.adapter = sessionAdapter


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}