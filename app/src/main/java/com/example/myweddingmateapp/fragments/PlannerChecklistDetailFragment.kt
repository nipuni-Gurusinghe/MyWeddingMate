package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.PlannerFavouriteAdapter
import com.example.myweddingmateapp.databinding.FragmentPlannerChecklistDetailBinding
import com.example.myweddingmateapp.dialog.BudgetDialog
import com.example.myweddingmateapp.models.PlannerFavouriteItem
import com.example.myweddingmateapp.models.User
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PlannerChecklistDetailFragment : Fragment() {
    private var _binding: FragmentPlannerChecklistDetailBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    private lateinit var adapter: PlannerFavouriteAdapter
    private var weddingDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlannerChecklistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = arguments?.getString("userId") ?: return
        setupRecyclerView()
        setupDetailScreen(userId)
    }

    private fun setupRecyclerView() {
        binding.recyclerChecklistItems.layoutManager = LinearLayoutManager(requireContext())
        adapter = PlannerFavouriteAdapter(
            emptyList(),
            onItemClick = { showItemDetails(it) },
            onFavoriteToggle = { toggleFavoriteStatus(it) },
            onBudgetClick = { showBudgetDialog(it) }
        )
        binding.recyclerChecklistItems.adapter = adapter
    }

    private fun setupDetailScreen(userId: String) {
        binding.progressBar.visibility = View.VISIBLE
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let {
                    binding.toolbar.title = "${it.name}'s Favorites"
                }
            }

        loadWeddingDate(userId)
        loadFavoriteItems(userId)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadWeddingDate(userId: String) {
        db.collection("couple_profiles").document(userId).get()
            .addOnSuccessListener { document ->
                val dateString = document.getString("weddingDate")
                weddingDate = try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                } catch (e: Exception) {
                    null
                }
            }
    }

    private fun loadFavoriteItems(userId: String) {
        db.collection("userFavorites")
            .document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener { documents ->
                val favorites = documents.mapNotNull {
                    PlannerFavouriteItem(
                        id = it.id,
                        userId = userId,
                        category = it.getString("category") ?: "Uncategorized",
                        vendorId = it.getString("vendorId") ?: "",
                        addedDate = it.getDate("addedDate")?.let { date -> dateFormat.format(date) } ?: "Unknown date",
                        budget = it.getDouble("budget") ?: 0.0,
                        currency = it.getString("currency") ?: "LKR",
                        isFavorite = it.getBoolean("isFavorite") ?: true,
                        notes = it.getString("notes") ?: "",
                        reminderDate = it.getDate("reminderDate")?.let { date -> dateFormat.format(date) } ?: "",
                        isCompleted = (it.getDouble("budget") ?: 0.0) > 0
                    )
                }
                adapter.updateItems(favorites)
                updateCounts(favorites)
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load items", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showItemDetails(item: PlannerFavouriteItem) {
    }

    private fun toggleFavoriteStatus(item: PlannerFavouriteItem) {
        val userId = arguments?.getString("userId") ?: return
        db.collection("userFavorites")
            .document(userId)
            .collection("items")
            .document(item.id)
            .update("isFavorite", !item.isFavorite)
            .addOnSuccessListener {
                loadFavoriteItems(userId)
            }
    }

    private fun showBudgetDialog(item: PlannerFavouriteItem) {
        BudgetDialog.show(
            requireContext(),
            item,
            weddingDate
        ) { budget, currency, date ->
            val updates = hashMapOf<String, Any>(
                "budget" to budget,
                "currency" to currency
            )

            date?.let { updates["reminderDate"] = it }

            db.collection("userFavorites")
                .document(item.userId)
                .collection("items")
                .document(item.id)
                .update(updates)
                .addOnSuccessListener { loadFavoriteItems(item.userId) }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateCounts(favorites: List<PlannerFavouriteItem>) {
        val totalCount = favorites.size
        val completedCount = favorites.count { it.budget > 0 }
        val totalBudget = favorites.sumOf { it.budget }

        binding.textTotalCount.text = totalCount.toString()
        binding.textCompletedCount.text = "$completedCount/$totalCount"
        binding.textTotalBudget.text = "LKR ${String.format("%.2f", totalBudget)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}