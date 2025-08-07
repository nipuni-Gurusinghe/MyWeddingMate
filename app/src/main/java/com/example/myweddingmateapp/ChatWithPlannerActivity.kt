package com.example.myweddingmateapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.adapters.PlannerChatListAdapter
import com.example.myweddingmateapp.fragments.PlannerChatFragment
import com.example.myweddingmateapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ChatWithPlannerActivity : BaseActivity() {

    companion object {
        private const val TAG = "ChatWithPlannerActivity"
    }

    private lateinit var recyclerChats: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var chatAdapter: PlannerChatListAdapter
    private val usersList = mutableListOf<User>()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var isShowingChat = false

    override fun getCurrentNavId(): Int = R.id.navChat
    override fun getLayoutResourceId(): Int = R.layout.activity_couple_dashboard
    override fun hasNavBar(): Boolean = !isShowingChat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeFirebase()

        if (savedInstanceState == null) {
            showChatList()
        } else {
            isShowingChat = savedInstanceState.getBoolean("isShowingChat", false)
            updateNavBarVisibility()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isShowingChat", isShowingChat)
    }

    private fun initializeFirebase() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    private fun showChatList() {
        isShowingChat = false
        updateNavBarVisibility()

        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val existingFragment = supportFragmentManager.findFragmentByTag("CHAT_LIST")
        if (existingFragment != null) {
            initializeChatListViews()
            loadSelectedPlanner()
            return
        }

        val chatListFragment = PlannerChatFragment().apply {
            arguments = Bundle().apply {
                putBoolean("isChatOpen", false)
                putBoolean("showChatList", true)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, chatListFragment, "CHAT_LIST")
            .commit()

        supportFragmentManager.executePendingTransactions()
        initializeChatListViews()
        loadSelectedPlanner()
    }

    private fun initializeChatListViews() {
        supportFragmentManager.findFragmentByTag("CHAT_LIST")?.view?.let { fragmentView ->
            try {
                recyclerChats = fragmentView.findViewById(R.id.recyclerChats)
                emptyState = fragmentView.findViewById(R.id.emptyState)
                setupRecyclerView()
                Log.d(TAG, "Chat list views initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing chat list views", e)
            }
        }
    }

    private fun setupRecyclerView() {
        if (::recyclerChats.isInitialized) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                chatAdapter = PlannerChatListAdapter(
                    allUsers = usersList,
                    currentUserRole = "User",
                    currentUserId = currentUser.uid
                ) { user ->
                    openChatWithPlanner(user)
                }

                recyclerChats.apply {
                    layoutManager = LinearLayoutManager(this@ChatWithPlannerActivity)
                    adapter = chatAdapter
                    setHasFixedSize(true)
                }
            }
        }
    }

    private fun loadSelectedPlanner() {
        val currentUser = auth.currentUser ?: run {
            showEmptyState()
            return
        }

        firestore.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { userDocument ->
                if (userDocument.exists()) {
                    val selectedPlannerId = userDocument.getString("selectedPlannerId")
                    if (!selectedPlannerId.isNullOrEmpty()) {
                        firestore.collection("users").document(selectedPlannerId)
                            .get()
                            .addOnSuccessListener { plannerDocument ->
                                try {
                                    val plannerUser = User(
                                        uid = selectedPlannerId,
                                        name = plannerDocument.getString("name") ?: "Unknown Planner",
                                        email = plannerDocument.getString("email") ?: "",
                                        role = "Wedding Planner",
                                        lastMessage = "Start a conversation with your wedding planner",
                                        lastMessageTime = getCurrentTime(),
                                        recipientId = currentUser.uid,
                                        unreadCount = 0
                                    )

                                    usersList.clear()
                                    usersList.add(plannerUser)
                                    chatAdapter.updateList(usersList)
                                    hideEmptyState()
                                } catch (e: Exception) {
                                    showEmptyState()
                                }
                            }
                            .addOnFailureListener { showEmptyState() }
                    } else {
                        showEmptyState()
                    }
                } else {
                    showEmptyState()
                }
            }
            .addOnFailureListener { showEmptyState() }
    }

    private fun openChatWithPlanner(user: User) {
        Log.d(TAG, "Opening chat with planner: ${user.name}")
        isShowingChat = true
        updateNavBarVisibility()

        try {
            val chatFragment = PlannerChatFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isChatOpen", true)
                    putBoolean("showChatList", false)
                    putString("recipientName", user.name)
                    putString("recipientId", user.uid)
                    putBoolean("fromPlannerActivity", true)
                }
            }

            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.container, chatFragment, "PLANNER_CHAT") // Using R.id.container consistently
                .addToBackStack("planner_chat")
                .commit()
        } catch (e: Exception) {
            Log.e(TAG, "Error opening chat", e)
            isShowingChat = false
            updateNavBarVisibility()
        }
    }

    private fun updateNavBarVisibility() {
        if (hasNavBar()) {
            findViewById<View>(R.id.navBar)?.visibility = View.VISIBLE
        } else {
            findViewById<View>(R.id.navBar)?.visibility = View.GONE
        }
    }

    private fun showEmptyState() {
        if (::emptyState.isInitialized && ::recyclerChats.isInitialized) {
            emptyState.visibility = View.VISIBLE
            recyclerChats.visibility = View.GONE
        }
    }

    private fun hideEmptyState() {
        if (::emptyState.isInitialized && ::recyclerChats.isInitialized) {
            emptyState.visibility = View.GONE
            recyclerChats.visibility = View.VISIBLE
        }
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    override fun onBackPressed() {
        if (isShowingChat) {
            isShowingChat = false
            updateNavBarVisibility()
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onResume() {
        super.onResume()
        updateNavBarVisibility()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ChatWithPlannerActivity destroyed")
    }
}