package com.example.myweddingmateapp

import android.content.Intent
import android.os.Bundle

class CoupleDashboardActivity : BaseActivity() {

    override fun getCurrentNavId(): Int = R.id.navHome

    override fun getLayoutResourceId(): Int = R.layout.activity_couple_dashboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        handleInitialNavigation()
    }

    private fun handleInitialNavigation() {

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}