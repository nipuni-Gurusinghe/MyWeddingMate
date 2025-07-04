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
        // You can add logic here to determine which screen to show first
        // For example, check if user is logged in, has completed onboarding, etc.

         // For now, let's redirect to HomeActivity by default
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}