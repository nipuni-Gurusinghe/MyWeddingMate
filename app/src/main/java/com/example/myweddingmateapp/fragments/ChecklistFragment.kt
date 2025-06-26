package com.example.myweddingmateapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.adapters.ChecklistAdapter
import com.example.myweddingmateapp.models.Client

class ChecklistFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var checklistAdapter: ChecklistAdapter
    private val clients = listOf(
        Client("Nadeesha & Sahan", R.drawable.ic_profile),
        Client("Ravi & Anjali", R.drawable.ic_profile),
        Client("Dilan & Kavindi", R.drawable.ic_profile)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_checklist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerChecklist)
        checklistAdapter = ChecklistAdapter(clients) { client -> }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = checklistAdapter
    }
}