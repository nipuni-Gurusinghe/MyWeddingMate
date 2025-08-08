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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class PlannerDashboardFragment : Fragment() {

    private var _binding: FragmentPlannerDashboardBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""
    private val allClients = mutableListOf<Client>()
    private val allBudgetItems = mutableListOf<String>()
    private val allReminderItems = mutableListOf<String>()

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
        fetchClients()
    }

    private fun setupRecyclerViews() {
        with(binding) {
            recyclerBudget.layoutManager = LinearLayoutManager(requireContext())
            recyclerReminders.layoutManager = LinearLayoutManager(requireContext())
            recyclerClients.layoutManager = LinearLayoutManager(requireContext())

            listOf(recyclerBudget, recyclerReminders, recyclerClients).forEach { recyclerView ->
                recyclerView.addItemDecoration(
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }
        }
    }

    private fun fetchClients() {
        db.collection("couple_profiles")
            .orderBy("weddingDate", Query.Direction.ASCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                allClients.clear()
                allClients.addAll(documents.mapNotNull { doc ->
                    val email = doc.getString("email") ?: return@mapNotNull null
                    val weddingDate = doc.getString("weddingDate") ?: ""
                    Client(doc.id, "Wedding on $weddingDate", email)
                })

                updateRecycler(binding.recyclerClients, binding.emptyClients,
                    PlannerClientAdapter(allClients) { }, allClients)

                fetchClientBudgetsAndReminders()
            }
            .addOnFailureListener {
                binding.recyclerClients.visibility = View.GONE
                binding.emptyClients.visibility = View.VISIBLE
            }
    }

    private fun fetchClientBudgetsAndReminders() {
        allBudgetItems.clear()
        allReminderItems.clear()

        val currentDate = Calendar.getInstance().time

        var processedClients = 0

        if (allClients.isEmpty()) {
            updateBudgetAndReminderViews()
            return
        }

        for (client in allClients) {
            db.collection("userFavorites")
                .document(client.uid)
                .collection("items")
                .whereGreaterThan("budget", 0.0)
                .orderBy("budget", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener { documents ->
                    documents.forEach { doc ->
                        val category = doc.getString("category") ?: "Uncategorized"
                        val budget = doc.getDouble("budget") ?: 0.0
                        val currency = doc.getString("currency") ?: "LKR"
                        allBudgetItems.add("$category: $currency $budget (${client.name})")
                    }

                    db.collection("userFavorites")
                        .document(client.uid)
                        .collection("items")
                        .whereGreaterThanOrEqualTo("reminderDate", currentDate)
                        .orderBy("reminderDate", Query.Direction.ASCENDING)
                        .limit(5)
                        .get()
                        .addOnSuccessListener { reminderDocs ->
                            reminderDocs.forEach { doc ->
                                val category = doc.getString("category") ?: "Uncategorized"
                                val reminderDate = doc.getDate("reminderDate")?.let {
                                    SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(it)
                                } ?: return@forEach
                                allReminderItems.add("$category - $reminderDate (${client.name})")
                            }

                            processedClients++
                            if (processedClients == allClients.size) {
                                updateBudgetAndReminderViews()
                            }
                        }
                }
                .addOnFailureListener {
                    processedClients++
                    if (processedClients == allClients.size) {
                        updateBudgetAndReminderViews()
                    }
                }
        }
    }

    private fun updateBudgetAndReminderViews() {
        if (allBudgetItems.isNotEmpty()) {
            val uniqueBudgetItems = allBudgetItems.distinct().take(5)
            binding.recyclerBudget.visibility = View.VISIBLE
            binding.emptyBudget.visibility = View.GONE
            binding.recyclerBudget.adapter = PlannerBudgetAdapter(uniqueBudgetItems)
        } else {
            binding.recyclerBudget.visibility = View.GONE
            binding.emptyBudget.visibility = View.VISIBLE
        }

        if (allReminderItems.isNotEmpty()) {
            val uniqueReminderItems = allReminderItems.distinct().take(5)
            binding.recyclerReminders.visibility = View.VISIBLE
            binding.emptyReminders.visibility = View.GONE
            binding.recyclerReminders.adapter = PlannerReminderAdapter(uniqueReminderItems)
        } else {
            binding.recyclerReminders.visibility = View.GONE
            binding.emptyReminders.visibility = View.VISIBLE
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