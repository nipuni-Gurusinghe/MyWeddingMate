package com.example.myweddingmateapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweddingmateapp.adapters.PhotographyAdapter
import com.example.myweddingmateapp.databinding.ActivityPhotographyBinding
import com.example.myweddingmateapp.models.Photography
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PhotographyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotographyBinding
    private lateinit var prefs: PrefsHelper
    private val photographyList = mutableListOf<Photography>()
    private val db = Firebase.firestore
    private lateinit var adapter: PhotographyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotographyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PrefsHelper.getInstance(this)

        setupBackButton()
        setupRecyclerView()
        fetchPhotographyData()
    }

    private fun setupRecyclerView() {
        adapter = PhotographyAdapter(
            photographyList = photographyList,
            onFavoriteClick = { photography ->
                photography.isFavorite = !photography.isFavorite
                if (photography.isFavorite) {
                    prefs.addFavorite(photography.id, "photography")
                } else {
                    prefs.removeFavorite(photography.id, "photography")
                }
                val position = photographyList.indexOfFirst { it.id == photography.id }
                if (position != -1) {
                    adapter.notifyItemChanged(position)
                }
            },
            onWebsiteClick = ::openWebsite
        )

        binding.photographyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PhotographyActivity)
            adapter = this@PhotographyActivity.adapter
        }
    }

    private fun fetchPhotographyData() {
        db.collection("photography")
            .get()
            .addOnSuccessListener { result ->
                photographyList.clear()
                result.documents.mapNotNull { doc ->
                    doc.toObject(Photography::class.java)?.apply {
                        isFavorite = prefs.isFavorite(id, "photography")
                    }
                }.let {
                    photographyList.addAll(it)
                    adapter.notifyItemRangeInserted(0, it.size)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PhotographyActivity", "Error loading data", exception)
                Toast.makeText(
                    this,
                    "Error loading photographers: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun openWebsite(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.toUrl())))
        } catch (e: Exception) {
            Log.e("PhotographyActivity", "Website open failed", e)
            Toast.makeText(this, "No app can handle this request", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun String.toUrl(): String {
        return if (startsWith("http://") || startsWith("https://")) {
            this
        } else {
            "https://$this"
        }
    }
}