package com.example.myweddingmateapp

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CheckListActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CheckListActivity"
    }

    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_list)

        Log.d(TAG, "CheckListActivity created successfully")

        initializeViews()
        setupClickListeners()
        setupContent()
    }

    private fun initializeViews() {
        try {

            btnBack = findViewById(R.id.btn_back)
            tvTitle = findViewById(R.id.tv_title)

            Log.d(TAG, "Views initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            createFallbackLayout()
        }
    }

    private fun setupClickListeners() {
        try {
            // Back btn
            btnBack.setOnClickListener {
                Log.d(TAG, "Back button clicked")
                onBackPressed()
            }

            Log.d(TAG, "Click listeners setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up click listeners", e)
        }
    }

    private fun setupContent() {
        try {

            tvTitle.text = "Wedding Check List"

            Log.d(TAG, "Content setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up content", e)
        }
    }

    private fun createFallbackLayout() {
        try {
            val textView = TextView(this).apply {
                text = "Wedding Check List"
                textSize = 18f
                setPadding(32, 32, 32, 32)
            }
            setContentView(textView)
            Log.d(TAG, "Fallback layout created")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating fallback layout", e)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        Log.d(TAG, "Back pressed")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "CheckListActivity destroyed")
    }
}