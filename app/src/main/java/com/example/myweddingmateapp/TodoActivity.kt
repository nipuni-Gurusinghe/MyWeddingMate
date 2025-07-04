package com.example.myweddingmateapp

import android.os.Bundle

class TodoActivity : BaseActivity() {

    override fun getCurrentNavId(): Int = R.id.navTodo

    override fun getLayoutResourceId(): Int = R.layout.activity_todo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initializeTodoComponents()
    }

    private fun initializeTodoComponents() {

    }
}