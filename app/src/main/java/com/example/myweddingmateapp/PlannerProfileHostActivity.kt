package com.example.myweddingmateapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myweddingmateapp.fragments.PlannerProfileFragment


class PlannerProfileHostActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PlannerProfileHost"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a simple layout programmatically
        val frameLayout = android.widget.FrameLayout(this).apply {
            id = android.R.id.content
        }
        setContentView(frameLayout)

        // Set up action bar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = intent.getStringExtra("planner_name") ?: "Planner Profile"
        }

        // Load the PlannerProfileFragment
        if (savedInstanceState == null) {
            val fragment = PlannerProfileFragment()

            // Pass data to fragment if needed
            val bundle = Bundle().apply {
                putString("planner_user_id", intent.getStringExtra("planner_user_id"))
                putString("planner_name", intent.getStringExtra("planner_name"))
                putString("planner_email", intent.getStringExtra("planner_email"))
            }
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}