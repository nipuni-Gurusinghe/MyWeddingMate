package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.adapters.ChatAdapter
import com.example.myweddingmateapp.adapters.ChatListAdapter
import com.example.myweddingmateapp.databinding.FragmentPlannerChatBinding
import com.example.myweddingmateapp.databinding.FragmentPlannerChatListBinding
import com.example.myweddingmateapp.models.ChatItem
import com.example.myweddingmateapp.models.ChatMessage
import com.google.android.material.snackbar.Snackbar

class ChatFragment : Fragment() {
    private var _chatBinding: FragmentPlannerChatBinding? = null
    private var _listBinding: FragmentPlannerChatListBinding? = null
    private val chatBinding get() = _chatBinding!!
    private val listBinding get() = _listBinding!!

    private val chatList = listOf(
        ChatItem("Nadeesha & Sahan", "Can we schedule a cake tasting?", "10:30 AM", R.drawable.ic_people),
        ChatItem("Elegant Florals", "Floral decor delivery confirmed", "Yesterday", R.drawable.ic_people),
        ChatItem("Ravi & Anjali", "Confirm pre-shoot appointment", "Jul 28", R.drawable.ic_people)
    )

    private val messageMap = mapOf(
        "Nadeesha & Sahan" to listOf(
            ChatMessage("Nadeesha & Sahan", "Hi there!", "10:30 AM", true),
            ChatMessage("You", "Hello! How can I help?", "10:32 AM", false),
            ChatMessage("Nadeesha & Sahan", "Can we schedule a cake tasting this Friday?", "10:33 AM", true)
        ),
        "Elegant Florals" to listOf(
            ChatMessage("Elegant Florals", "Your floral order is ready!", "9:15 AM", true),
            ChatMessage("You", "Great! When's the delivery date?", "9:20 AM", false),
            ChatMessage("Elegant Florals", "Delivery confirmed for August 11", "9:22 AM", true)
        ),
        "Ravi & Anjali" to listOf(
            ChatMessage("You", "Hi! About your pre-shoot...", "Jul 28", false),
            ChatMessage("Ravi & Anjali", "Yes, please confirm our appointment", "Jul 28", true)
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (arguments?.getBoolean("isChatOpen", false) == true) {
            _chatBinding = FragmentPlannerChatBinding.inflate(inflater, container, false)
            setupChatScreen(arguments?.getString("recipientName") ?: "")
            chatBinding.root
        } else {
            _listBinding = FragmentPlannerChatListBinding.inflate(inflater, container, false)
            setupChatList()
            listBinding.root
        }
    }

    private fun setupChatList() {
        listBinding.recyclerChats.layoutManager = LinearLayoutManager(requireContext())
        listBinding.recyclerChats.adapter = ChatListAdapter(chatList) { chatItem ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ChatFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean("isChatOpen", true)
                        putString("recipientName", chatItem.name)
                    }
                })
                .addToBackStack(null)
                .commit()
        }

        listBinding.fabNewChat.setOnClickListener {
            Snackbar.make(listBinding.root, "New chat started", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupChatScreen(recipientName: String) {
        chatBinding.toolbar.title = recipientName
        chatBinding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        chatBinding.txtRecipientName.text = recipientName
        chatBinding.txtRecipientStatus.text = "Online"

        val messages = messageMap[recipientName] ?: emptyList()
        chatBinding.recyclerChat.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        chatBinding.recyclerChat.adapter = ChatAdapter(messages)

        chatBinding.btnSend.setOnClickListener {
            val message = chatBinding.editMessage.text.toString()
            if (message.isNotEmpty()) {
                Snackbar.make(chatBinding.root, "Message sent: $message", Snackbar.LENGTH_SHORT).show()
                chatBinding.editMessage.text?.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _chatBinding = null
        _listBinding = null
    }
}