package com.example.myweddingmateapp



import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginTextView = findViewById<TextView>(R.id.loginTextView)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)
        val termsCheckBox = findViewById<CheckBox>(R.id.termsCheckBox)

        // Setup the role spinner
        val roles = arrayOf("User", "Wedding Planner", "Vendor")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        registerButton.setOnClickListener {
            // Get selected role
            val selectedRole = roleSpinner.selectedItem.toString()

            // Basic validation example
            if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "Please accept terms & conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Here you would typically:
            // 1. Validate all fields
            // 2. Register the user with their selected role
            // 3. Navigate to appropriate screen based on role

            // For now, just show the selected role
            Toast.makeText(this, "Registering as: $selectedRole", Toast.LENGTH_SHORT).show()

            // After successful registration, you might do:
             val intent = Intent(this, PlannerDashboardActivity::class.java)
             startActivity(intent)
             finish()
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}