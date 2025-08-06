package com.example.myweddingmateapp.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.models.FavoriteItem

class BudgetDialog(
    private val context: Context,
    private val favoriteItem: FavoriteItem,
    private val onBudgetSaved: (Double) -> Unit
) : Dialog(context) {

    init {
        setContentView(R.layout.dialog_budget)
        setupViews()
    }

    private fun setupViews() {
        val editBudget = findViewById<EditText>(R.id.editBudget)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)



        btnSave.setOnClickListener {
            val budget = editBudget.text.toString().toDoubleOrNull() ?: 0.0
            if (budget >= 0) {
                onBudgetSaved(budget)
                dismiss()
            } else {
                editBudget.error = "Please enter a valid amount"
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }
}