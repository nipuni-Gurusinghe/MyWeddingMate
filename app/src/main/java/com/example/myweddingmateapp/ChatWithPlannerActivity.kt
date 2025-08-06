package com.example.myweddingmateapp

import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class ChatWithPlannerActivity : BaseActivity() {

    // Chat UI components
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerChat: RecyclerView
    private lateinit var editMessage: TextInputEditText
    private lateinit var btnSend: FloatingActionButton
    private lateinit var btnAttachment: ImageButton
    private lateinit var btnEmoji: ImageButton
    private lateinit var txtRecipientName: TextView
    private lateinit var txtRecipientStatus: TextView
    private lateinit var imgRecipient: ImageView

    override fun getCurrentNavId(): Int = R.id.navChat

    override fun getLayoutResourceId(): Int = R.layout.fragment_planner_chat

    // This activity doesn't have navbar in its XML layout
    override fun hasNavBar(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeChatComponents()
        setupChatUI()
    }

    private fun initializeChatComponents() {
        // Initialize all chat UI components
        toolbar = findViewById(R.id.toolbar)
        recyclerChat = findViewById(R.id.recyclerChat)
        editMessage = findViewById(R.id.editMessage)
        btnSend = findViewById(R.id.btnSend)
        btnAttachment = findViewById(R.id.btnAttachment)
        btnEmoji = findViewById(R.id.btnEmoji)
        txtRecipientName = findViewById(R.id.txtRecipientName)
        txtRecipientStatus = findViewById(R.id.txtRecipientStatus)
        imgRecipient = findViewById(R.id.imgRecipient)
    }

    private fun setupChatUI() {
        // Setup toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Handle back navigation - redirect to HomeActivity
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }


        recyclerChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true // Start from bottom
        }


        setupRecipientInfo()


        setupMessageInput()
    }

    private fun setupRecipientInfo() {
        // Set planner name and status - you can get this from Intent extras
        val plannerName = intent.getStringExtra("planner_name") ?: "Wedding Planner"
        val plannerStatus = intent.getStringExtra("planner_status") ?: "Online"

        txtRecipientName.text = plannerName
        txtRecipientStatus.text = plannerStatus


    }

    private fun setupMessageInput() {

        btnSend.setOnClickListener {
            sendMessage()
        }


        btnAttachment.setOnClickListener {
            handleAttachmentSelection()
        }


        btnEmoji.setOnClickListener {
            handleEmojiSelection()
        }


        editMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                btnSend.show()
            }
        }


        editMessage.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    btnSend.show()
                } else {
                    btnSend.hide()
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun sendMessage() {
        val messageText = editMessage.text.toString().trim()
        if (messageText.isNotEmpty()) {

            editMessage.text?.clear()
            btnSend.hide()


        }
    }

    private fun handleAttachmentSelection() {


    }

    private fun handleEmojiSelection() {


    }

    // Handle results from attachment selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }

    companion object {
        private const val ATTACHMENT_REQUEST_CODE = 100
    }
}