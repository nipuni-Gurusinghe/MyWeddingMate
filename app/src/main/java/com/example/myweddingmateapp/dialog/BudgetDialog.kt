package com.example.myweddingmateapp.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.myweddingmateapp.R
import com.example.myweddingmateapp.databinding.DialogBudgetBinding
import com.example.myweddingmateapp.models.PlannerFavouriteItem
import java.text.SimpleDateFormat
import java.util.*

class BudgetDialog(
    context: Context,
    private val favoriteItem: PlannerFavouriteItem,
    private val weddingDate: Date?,
    private val onBudgetSaved: (Double, String, Date?) -> Unit
) : AlertDialog(context) {

    private lateinit var binding: DialogBudgetBinding
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private var selectedCurrency = "USD"
    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        setupCurrencySpinner()
        loadExistingData()
    }

    private fun setupViews() {
        binding.editReminderDate.setOnClickListener {
            showDatePicker()
        }
        binding.btnSave.setOnClickListener {
            saveBudgetAndReminder()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupCurrencySpinner() {
        val currencies = context.resources.getStringArray(R.array.currencies_array)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = adapter

        binding.spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCurrency = currencies[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadExistingData() {
        favoriteItem.budget.takeIf { it > 0 }?.let {
            binding.editBudget.setText(it.toString())
        }

        favoriteItem.currency?.let { currency ->
            val currencies = context.resources.getStringArray(R.array.currencies_array)
            val position = currencies.indexOf(currency)
            if (position >= 0) {
                binding.spinnerCurrency.setSelection(position)
            }
        }

        favoriteItem.reminderDate?.let { dateString ->
            try {
                val inputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                val parsedDate = inputFormat.parse(dateString)
                selectedDate = parsedDate
                binding.editReminderDate.setText(dateFormat.format(parsedDate))
            } catch (e: Exception) {
                try {
                    val fallbackFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val parsedDate = fallbackFormat.parse(dateString)
                    selectedDate = parsedDate
                    binding.editReminderDate.setText(dateFormat.format(parsedDate))
                } catch (e: Exception) {
                    binding.editReminderDate.setText("")
                }
            }
        }
    }

    private fun showDatePicker() {
        val initialYear = selectedDate?.let {
            val cal = Calendar.getInstance()
            cal.time = it
            cal.get(Calendar.YEAR)
        } ?: calendar.get(Calendar.YEAR)

        val initialMonth = selectedDate?.let {
            val cal = Calendar.getInstance()
            cal.time = it
            cal.get(Calendar.MONTH)
        } ?: calendar.get(Calendar.MONTH)

        val initialDay = selectedDate?.let {
            val cal = Calendar.getInstance()
            cal.time = it
            cal.get(Calendar.DAY_OF_MONTH)
        } ?: calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val selected = calendar.time

                if (weddingDate != null && selected.after(weddingDate)) {
                    Toast.makeText(context, "Reminder date cannot be after wedding date", Toast.LENGTH_SHORT).show()
                    return@DatePickerDialog
                }

                selectedDate = selected
                binding.editReminderDate.setText(dateFormat.format(selected))
            },
            initialYear,
            initialMonth,
            initialDay
        )

        if (weddingDate != null) {
            datePicker.datePicker.maxDate = weddingDate.time
        }

        datePicker.show()
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

        onBudgetSaved(amount, selectedCurrency, selectedDate)
        dismiss()
    }

    companion object {
        fun show(
            context: Context,
            favoriteItem: PlannerFavouriteItem,
            weddingDate: Date?,
            onBudgetSaved: (Double, String, Date?) -> Unit
        ) {
            BudgetDialog(context, favoriteItem, weddingDate, onBudgetSaved).show()
        }
    }
}