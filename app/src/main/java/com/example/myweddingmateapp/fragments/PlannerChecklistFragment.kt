package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.adapters.UserListAdapter
import com.example.myweddingmateapp.databinding.FragmentPlannerChecklistBinding
import com.example.myweddingmateapp.models.User
import com.google.firebase.firestore.FirebaseFirestore

class PlannerChecklistFragment : Fragment() {
    private var _binding: FragmentPlannerChecklistBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlannerChecklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListScreen()
    }

    private fun setupListScreen() {
        binding.recyclerChecklist.layoutManager = LinearLayoutManager(requireContext())

        db.collection("users")
            .whereEqualTo("role", "User")
            .get()
            .addOnSuccessListener { documents ->
                val users = documents.mapNotNull {
                    User(
                        uid = it.id,
                        name = it.getString("name") ?: "No Name",
                        email = it.getString("email") ?: "",
                        profileImage = it.getString("profileImage") ?: ""
                    )
                }

                binding.recyclerChecklist.adapter = UserListAdapter(users) { user ->
                    val detailFragment = PlannerChecklistDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString("userId", user.uid)
                        }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, detailFragment)
                        .addToBackStack(null)
                        .commit()
                }

                updateCounters(users.size)
            }
    }

    private fun updateCounters(total: Int) {
        binding.textTotalCount.text = total.toString()
        binding.textCompletedCount.text = "0"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}