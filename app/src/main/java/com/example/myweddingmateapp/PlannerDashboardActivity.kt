package com.example.myweddingmateapp

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myweddingmateapp.fragments.PlannerChatFragment
import com.example.myweddingmateapp.fragments.PlannerChecklistFragment
import com.example.myweddingmateapp.fragments.PlannerDashboardFragment
import com.example.myweddingmateapp.fragments.PlannerProfileFragment

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

        loadFragment(PlannerDashboardFragment())

        navHome.setOnClickListener {
            loadFragment(PlannerDashboardFragment())
        }

        navChecklist.setOnClickListener {
            loadFragment(PlannerChecklistFragment())
        }

        navChat.setOnClickListener {
            loadFragment(PlannerChatFragment())
        }

        navProfile.setOnClickListener {
            loadFragment(PlannerProfileFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
