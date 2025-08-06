package com.example.myweddingmateapp.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.databinding.DialogBudgetBinding
import com.example.myweddingmateapp.models.PlannerFavouriteItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class BudgetDialog(
    context: Context,
    private val favoriteItem: PlannerFavouriteItem,
    private val onBudgetSaved: (Double, String) -> Unit
) : AlertDialog(context) {

    private lateinit var binding: DialogBudgetBinding
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private var selectedCurrency = "USD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        setupCurrencyDropdown()
        loadExistingData()
    }

    private fun setupViews() {
        binding.editReminderDate.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { saveBudgetAndReminder() }
        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun setupCurrencyDropdown() {
        val currencies = arrayOf("USD", "EUR", "LKR", "GBP", "JPY")
        val adapter = ArrayAdapter(context, R.layout.dropdown_item, currencies)
        binding.autoCompleteCurrency.setAdapter(adapter)
        binding.autoCompleteCurrency.setOnItemClickListener { _, _, position, _ ->
            selectedCurrency = currencies[position]
        }
    }

    private fun loadExistingData() {
        if (favoriteItem.budget > 0) binding.editBudget.setText(favoriteItem.budget.toString())
        favoriteItem.currency?.let {
            selectedCurrency = it
            binding.autoCompleteCurrency.setText(it, false)
        }
        favoriteItem.reminderDate?.let {
            binding.editReminderDate.setText(dateFormat.format(it))
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                binding.editReminderDate.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveBudgetAndReminder() {
        val amountText = binding.editBudget.text.toString()
        if (amountText.isBlank()) {
            Toast.makeText(context, "Please enter budget amount", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = try {
            amountText.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Invalid amount format", Toast.LENGTH_SHORT).show()
            return
        }

        onBudgetSaved(amount, selectedCurrency)
        dismiss()
    }

    companion object {
        fun show(
            context: Context,
            favoriteItem: PlannerFavouriteItem,
            onBudgetSaved: (Double, String) -> Unit
        ): BudgetDialog {
            return BudgetDialog(context, favoriteItem, onBudgetSaved).apply {
                window?.setBackgroundDrawableResource(android.R.color.white)
                show()
            }
        }
    }
}