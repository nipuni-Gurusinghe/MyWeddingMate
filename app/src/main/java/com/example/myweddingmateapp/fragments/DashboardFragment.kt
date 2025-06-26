package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myweddingmateapp.R

class DashboardFragment : Fragment() {

    private lateinit var textBudget: TextView
    private lateinit var textReminder: TextView
    private lateinit var textClients: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textBudget = view.findViewById(R.id.textBudget)
        textReminder = view.findViewById(R.id.textReminder)
        textClients = view.findViewById(R.id.textClients)

        loadDashboardData()
    }

    private fun loadDashboardData() {
        // Later: Fetch from Firebase Firestore or Realtime Database

        // Example:
        // FirebaseFirestore.getInstance().collection("planner_dashboard")
        //     .document("userId123")
        //     .get()
        //     .addOnSuccessListener { document ->
        //         textBudget.text = document.getString("budgetSummary")
        //         textReminder.text = document.getString("reminder")
        //         textClients.text = document.getString("newClients")
        //     }

        textBudget.text = "You’ve spent Rs. 450,000 this month"
        textReminder.text = "Cake tasting – Friday 10 AM"
        textClients.text = "1. Nadeesha & Sahan – Aug 12\n2. Ravi & Anjali – Sept 5"
    }
}
