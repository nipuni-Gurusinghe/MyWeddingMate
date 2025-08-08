package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.adapters.PlannerChatAdapter
import com.example.myweddingmateapp.adapters.PlannerChatListAdapter
import com.example.myweddingmateapp.databinding.FragmentPlannerChatBinding
import com.example.myweddingmateapp.databinding.FragmentPlannerChatListBinding
import com.example.myweddingmateapp.models.ChatMessage
import com.example.myweddingmateapp.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class PlannerChatFragment : Fragment() {
    private var _chatBinding: FragmentPlannerChatBinding? = null
    private var _listBinding: FragmentPlannerChatListBinding? = null
    private val chatBinding get() = _chatBinding!!
    private val listBinding get() = _listBinding!!

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var usersListener: ListenerRegistration? = null
    private var chatListener: ListenerRegistration? = null
    private var currentUser: User? = null
    private val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateDayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (arguments?.getBoolean("isChatOpen", false) == true) {
            _chatBinding = FragmentPlannerChatBinding.inflate(inflater, container, false)
            setupChatScreen(arguments?.getString("recipientId") ?: "")
            chatBinding.root
        } else {
            _listBinding = FragmentPlannerChatListBinding.inflate(inflater, container, false)
            loadCurrentUser()
            listBinding.root
        }
    }

    private fun loadCurrentUser() {
        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    currentUser = document.toObject(User::class.java)
                    Log.e("CurrentUser", currentUser.toString())
                    currentUser?.let { setupChatList(it) }
                }
        }
    }

    private fun setupChatList(user: User) {
        listBinding.recyclerChats.layoutManager = LinearLayoutManager(requireContext())

        val query = if (user.role == "Wedding Planner") {
            db.collection("users")
                .whereEqualTo("role", "User")
                .whereEqualTo("selectedPlannerId", user.uid)
        } else {
            if (user.selectedPlannerId != null && user.selectedPlannerId!!.isNotEmpty()) {
                user.selectedPlannerId?.let { plannerId ->
                    db.collection("users")
                        .whereEqualTo("role", "Wedding Planner")
                        .whereEqualTo(FieldPath.documentId(), plannerId)
                }
            } else {
                db.collection("users").whereEqualTo("role", "NO_RESULTS")
            }
        }

        usersListener = query?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ChatList", "Error fetching users", error)
                return@addSnapshotListener
            }

            val users = snapshot?.documents?.mapNotNull {
                it.toObject(User::class.java)?.copy(uid = it.id)
            } ?: emptyList()

            val adapter = PlannerChatListAdapter(
                users,
                user.role,
                user.uid
            ) { selectedUser ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, PlannerChatFragment().apply {
                        arguments = Bundle().apply {
                            putBoolean("isChatOpen", true)
                            putString("recipientId", selectedUser.uid)
                        }
                    })
                    .addToBackStack(null)
                    .commit()
            }

            listBinding.recyclerChats.adapter = adapter

            users.forEach { recipient ->
                val chatId = if (user.uid < recipient.uid) {
                    "${user.uid}-${recipient.uid}"
                } else {
                    "${recipient.uid}-${user.uid}"
                }

                db.collection("chats").document(chatId)
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .addSnapshotListener { messageSnapshot, messageError ->
                        if (messageError != null) {
                            Log.e("ChatList", "Error fetching last message", messageError)
                            return@addSnapshotListener
                        }

                        messageSnapshot?.documents?.firstOrNull()?.let { doc ->
                            val data = doc.data
                            val lastMessage = data?.get("message") as? String ?: ""
                            val timestamp = data?.get("timestamp") as? Long ?: 0L
                            val recipientId = data?.get("recipientId") as? String ?: ""
                            adapter.updateLastMessage(
                                recipient.uid,
                                lastMessage,
                                formatTimestamp(timestamp),
                                recipientId
                            )
                        }
                    }
            }
        }
    }

    private fun setupChatScreen(recipientId: String) {
        var recipientName = ""

        db.collection("users").document(recipientId).get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let { recipient ->
                    recipientName = recipient.name
                    chatBinding.toolbar.title = recipientName
                    chatBinding.txtRecipientName.text = recipientName
                    chatBinding.txtRecipientStatus.text = ""
                }
            }

        val currentUserId = auth.currentUser?.uid ?: return
        val chatId = if (currentUserId < recipientId) {
            "$currentUserId-$recipientId"
        } else {
            "$recipientId-$currentUserId"
        }

        chatListener = db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data
                    ChatMessage(
                        senderId = data?.get("senderId") as? String ?: "",
                        recipientId = data?.get("recipientId") as? String ?: "",
                        senderName = data?.get("senderName") as? String ?: "",
                        message = data?.get("message") as? String ?: "",
                        displayTime = formatTimestamp(data?.get("timestamp") as? Long ?: 0L),
                        isReceived = data?.get("senderId") != currentUserId,
                        timestamp = data?.get("timestamp") as? Long ?: 0L,
                        isRead = data?.get("isRead") as? Boolean ?: false,
                        messageType = data?.get("messageType") as? String ?: "text",
                        mediaUrl = data?.get("mediaUrl") as? String
                    )
                } ?: emptyList()

                chatBinding.recyclerChat.layoutManager = LinearLayoutManager(requireContext()).apply {
                    stackFromEnd = true
                }
                chatBinding.recyclerChat.adapter = PlannerChatAdapter(messages).also {
                    if (messages.isNotEmpty()) {
                        chatBinding.recyclerChat.scrollToPosition(messages.size - 1)
                    }
                }
            }

        chatBinding.editMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                chatBinding.btnSend.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        chatBinding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        chatBinding.btnSend.setOnClickListener {
            val messageText = chatBinding.editMessage.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessage(recipientId, recipientName, messageText)
                chatBinding.editMessage.text?.clear()
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        return if (isToday(date)) {
            dateFormat.format(date)
        } else {
            dateDayFormat.format(date)
        }
    }

    private fun isToday(date: Date): Boolean {
        val today = Date()
        return date.year == today.year &&
                date.month == today.month &&
                date.date == today.date
    }

    private fun sendMessage(recipientId: String, recipientName: String, text: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val currentUserName = currentUser?.name ?: "You"
        val chatId = if (currentUserId < recipientId) {
            "$currentUserId-$recipientId"
        } else {
            "$recipientId-$currentUserId"
        }

        val message = hashMapOf(
            "senderId" to currentUserId,
            "recipientId" to recipientId,
            "senderName" to currentUserName,
            "message" to text,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false,
            "messageType" to "text"
        )

        db.collection("chats").document(chatId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                db.collection("chats").document(chatId)
                    .set(hashMapOf("chatId" to chatId))
                    .addOnSuccessListener {
                        Snackbar.make(chatBinding.root, "Message sent", Snackbar.LENGTH_SHORT).show()
                    }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        usersListener?.remove()
        chatListener?.remove()
        _chatBinding = null
        _listBinding = null
    }
}