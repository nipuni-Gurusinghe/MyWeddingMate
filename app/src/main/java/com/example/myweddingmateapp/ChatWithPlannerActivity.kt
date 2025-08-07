package com.example.myweddingmateapp

class ChatWithPlannerActivity : BaseActivity() {

    override fun getCurrentNavId(): Int = R.id.navChat
    override fun getLayoutResourceId(): Int = R.layout.fragment_planner_chat_list
    override fun hasNavBar(): Boolean = true
    override fun needsProgrammaticNavbar(): Boolean = true

}