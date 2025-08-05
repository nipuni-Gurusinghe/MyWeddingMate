package com.example.myweddingmateapp

import android.os.Bundle

class ChatWithPlannerActivity : BaseActivity() {

    override fun getCurrentNavId(): Int = R.id.navChat

    override fun getLayoutResourceId(): Int = R.layout.activity_chat_planner  //chat with selected planner screen ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initializeTodoComponents()
    }

    private fun initializeTodoComponents() {

    }
}