package com.example.myweddingmateapp

import android.os.Bundle
import com.example.myweddingmateapp.fragments.PlannerChatFragment

class ChatWithPlannerActivity : BaseActivity() {

    override fun getCurrentNavId(): Int = R.id.navChat
    override fun getLayoutResourceId(): Int = R.layout.activity_chat_planner
    override fun hasNavBar(): Boolean = true
    override fun needsProgrammaticNavbar(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            loadPlannerChatFragment()
        }
    }

    private fun loadPlannerChatFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlannerChatFragment())
            .commit()
    }

}