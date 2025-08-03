package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.FavoriteItemAdapter
import com.example.myweddingmateapp.databinding.FragmentPlannerChecklistDetailBinding
import com.example.myweddingmateapp.models.FavoriteItem

import com.example.myweddingmateapp.models.User
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PlannerChecklistDetailFragment : Fragment() {
    private var _binding: FragmentPlannerChecklistDetailBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy hh:mm:ss a", Locale.getDefault())
    private lateinit var adapter: FavoriteItemAdapter

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
        adapter = FavoriteItemAdapter(emptyList()) { }
        binding.recyclerChecklistItems.adapter = adapter
    }

    private fun setupDetailScreen(userId: String) {
        binding.progressBar.visibility = View.VISIBLE

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let { user ->
                    binding.toolbar.title = "${user.name}'s Favorites"
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
                val favorites = documents.mapNotNull { doc ->
                    FavoriteItem(
                        id = doc.id,
                        category = doc.getString("category") ?: "",
                        favoriteId = doc.getString("favoriteId") ?: "",
                        itemId = doc.getString("itemId") ?: "",
                        timestamp = doc.getDate("timestamp")?.let { dateFormat.format(it) } ?: ""
                    )
                }
                adapter = FavoriteItemAdapter(favorites) { }
                binding.recyclerChecklistItems.adapter = adapter
                updateFavoriteCount(favorites.size)
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun updateFavoriteCount(count: Int) {
        binding.textTotalCount.text = count.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}