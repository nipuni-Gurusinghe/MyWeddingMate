package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.PlannerFavouriteAdapter
import com.example.myweddingmateapp.databinding.FragmentPlannerChecklistDetailBinding
import com.example.myweddingmateapp.models.PlannerFavouriteItem
import com.example.myweddingmateapp.models.User
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PlannerChecklistDetailFragment : Fragment() {
    private var _binding: FragmentPlannerChecklistDetailBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private lateinit var adapter: PlannerFavouriteAdapter
    private var totalBudget: Double = 0.0

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
            onFavoriteToggle = { toggleFavoriteStatus(it) }
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

        loadFavoriteItems(userId)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
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
                        category = it.getString("category") ?: "Uncategorized",
                        vendorId = it.getString("vendorId") ?: "",
                        addedDate = it.getDate("addedDate")?.let { date -> dateFormat.format(date) } ?: "Unknown date",
                        budget = it.getDouble("budget") ?: 0.0,
                        isFavorite = it.getBoolean("isFavorite") ?: true,
                        notes = it.getString("notes") ?: ""
                    )
                }
                adapter.updateItems(favorites)
                updateCounts(favorites)
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun showItemDetails(item: PlannerFavouriteItem) {
        // Implement item details display
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

    private fun updateCounts(favorites: List<PlannerFavouriteItem>) {
        binding.textTotalCount.text = favorites.size.toString()
        binding.textCompletedCount.text = favorites.count { it.isFavorite }.toString()
        binding.textTotalBudget.text = currencyFormat.format(favorites.sumOf { it.budget })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}