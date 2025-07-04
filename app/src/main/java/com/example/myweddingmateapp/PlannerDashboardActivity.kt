package com.example.myweddingmateapp

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myweddingmateapp.fragments.ChatFragment
import com.example.myweddingmateapp.fragments.ChecklistFragment
import com.example.myweddingmateapp.fragments.DashboardFragment
import com.example.myweddingmateapp.fragments.ProfileFragment

class PlannerDashboardActivity : AppCompatActivity() {

    private lateinit var navHome: LinearLayout
    private lateinit var navChecklist: LinearLayout
    private lateinit var navChat: LinearLayout
    private lateinit var navProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner_dashboard)

        navHome = findViewById(R.id.navHome)
        navChecklist = findViewById(R.id.navChecklist)
        navChat = findViewById(R.id.navChat)
        navProfile = findViewById(R.id.navProfile)

        loadFragment(DashboardFragment())

        navHome.setOnClickListener {
            loadFragment(DashboardFragment())
        }

        navChecklist.setOnClickListener {
            loadFragment(ChecklistFragment())
        }

        navChat.setOnClickListener {
            loadFragment(ChatFragment())
        }

        navProfile.setOnClickListener {
            loadFragment(ProfileFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
