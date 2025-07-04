package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.adapters.PlannerBudgetAdapter
import com.example.myweddingmateapp.adapters.PlannerClientAdapter
import com.example.myweddingmateapp.adapters.PlannerReminderAdapter

class PlannerDashboardFragment : Fragment() {

    private lateinit var recyclerBudget: RecyclerView
    private lateinit var recyclerReminders: RecyclerView
    private lateinit var recyclerClients: RecyclerView
    private lateinit var emptyBudget: LinearLayout
    private lateinit var emptyReminders: LinearLayout
    private lateinit var emptyClients: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_planner_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerBudget = view.findViewById(R.id.recyclerBudget)
        recyclerReminders = view.findViewById(R.id.recyclerReminders)
        recyclerClients = view.findViewById(R.id.recyclerClients)
        emptyBudget = view.findViewById(R.id.emptyBudget)
        emptyReminders = view.findViewById(R.id.emptyReminders)
        emptyClients = view.findViewById(R.id.emptyClients)

        setupRecyclerViews()
        loadDashboardData()
    }

    private fun setupRecyclerViews() {
        recyclerBudget.layoutManager = LinearLayoutManager(requireContext())
        recyclerReminders.layoutManager = LinearLayoutManager(requireContext())
        recyclerClients.layoutManager = LinearLayoutManager(requireContext())

        recyclerBudget.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        recyclerReminders.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        recyclerClients.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    private fun loadDashboardData() {
        val budgetItems = listOf(
            "You've spent Rs. 450,000 this month",
            "Remaining budget: Rs. 150,000",
            "Most expensive category: Venue (Rs. 300,000)"
        )
        updateRecycler(recyclerBudget, emptyBudget, PlannerBudgetAdapter(budgetItems), budgetItems)

        val reminderItems = listOf(
            "Cake tasting – Friday 10 AM",
            "Venue visit – Saturday 2 PM",
            "Dress fitting – Next Wednesday"
        )
        updateRecycler(recyclerReminders, emptyReminders, PlannerReminderAdapter(reminderItems), reminderItems)

        val clientItems = listOf(
            "Nadeesha & Sahan – Aug 12",
            "Ravi & Anjali – Sept 5",
            "Priya & Arun – Oct 15"
        )
        updateRecycler(recyclerClients, emptyClients, PlannerClientAdapter(clientItems), clientItems)
    }

    private fun updateRecycler(
        recyclerView: RecyclerView,
        emptyView: LinearLayout,
        adapter: RecyclerView.Adapter<*>,
        items: List<*>
    ) {
        if (items.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            recyclerView.adapter = adapter
        } else {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }
}