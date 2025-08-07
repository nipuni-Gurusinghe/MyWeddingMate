package com.example.myweddingmateapp

import android.os.Bundle

// Chat with selected planner .......
class TodoActivity : BaseActivity() {


    override fun getCurrentNavId(): Int = R.id.navTodo //chat ID

    override fun getLayoutResourceId(): Int = R.layout.activity_todo  // chat xml

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initializeTodoComponents()
    }

    private fun initializeTodoComponents() {

    }
}