package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.adapters.PlannerBudgetAdapter
import com.example.myweddingmateapp.adapters.PlannerClientAdapter
import com.example.myweddingmateapp.adapters.PlannerReminderAdapter
import com.example.myweddingmateapp.databinding.FragmentPlannerDashboardBinding
import com.example.myweddingmateapp.models.Client
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class PlannerDashboardFragment : Fragment() {

    private var _binding: FragmentPlannerDashboardBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlannerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        loadDashboardData()
        fetchClientsFromDatabase()
    }

    private fun setupRecyclerViews() {
        with(binding) {
            recyclerBudget.layoutManager = LinearLayoutManager(requireContext())
            recyclerReminders.layoutManager = LinearLayoutManager(requireContext())
            recyclerClients.layoutManager = LinearLayoutManager(requireContext())

            listOf(recyclerBudget, recyclerReminders, recyclerClients).forEach { recyclerView ->
                recyclerView.addItemDecoration(
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                )
            }
        }
    }

    private fun loadDashboardData() {
        val budgetItems = listOf(
            "You've spent Rs. 450,000 this month",
            "Remaining budget: Rs. 150,000",
            "Most expensive category: Venue (Rs. 300,000)"
        )
        updateRecycler(binding.recyclerBudget, binding.emptyBudget,
            PlannerBudgetAdapter(budgetItems), budgetItems)

        val reminderItems = listOf(
            "Cake tasting – Friday 10 AM",
            "Venue visit – Saturday 2 PM",
            "Dress fitting – Next Wednesday"
        )
        updateRecycler(binding.recyclerReminders, binding.emptyReminders,
            PlannerReminderAdapter(reminderItems), reminderItems)
    }

    private fun fetchClientsFromDatabase() {
        db.collection("couple_profiles")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                val clients = documents.mapNotNull { doc ->
                    val email = doc.getString("email") ?: return@mapNotNull null
                    val createdAt = doc.getLong("createdAt")?.let {
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                    } ?: "Unknown date"
                    Client(
                        uid = doc.id,
                        name = "Couple - $createdAt",
                        email = email
                    )
                }
                updateRecycler(
                    binding.recyclerClients,
                    binding.emptyClients,
                    PlannerClientAdapter(clients) { /* Handle click */ },
                    clients
                )
            }
            .addOnFailureListener {
                binding.recyclerClients.visibility = View.GONE
                binding.emptyClients.visibility = View.VISIBLE
            }
    }

    private fun updateRecycler(
        recyclerView: RecyclerView,
        emptyView: View,
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}