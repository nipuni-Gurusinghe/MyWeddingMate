package com.example.myweddingmateapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailEditText.error = "Email required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordEditText.error = "Password required"
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        binding.loginButton.isEnabled = false
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkUserRole()
                } else {
                    binding.loginButton.isEnabled = true
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserRole() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                when (document.getString("role")) {
                    "Wedding Planner" -> {
                        startActivity(Intent(this, PlannerDashboardActivity::class.java))
                        finish()
                    }
                    else -> {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(this, "Wedding Planners only", Toast.LENGTH_LONG).show()
                        binding.loginButton.isEnabled = true
                    }
                }
            }
            .addOnFailureListener {
                FirebaseAuth.getInstance().signOut()
                binding.loginButton.isEnabled = true
            }
    }
}