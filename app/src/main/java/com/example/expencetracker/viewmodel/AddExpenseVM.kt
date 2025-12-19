package com.example.expencetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.notification.NotificationHelper
import com.example.expencetracker.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddExpenseVM @Inject constructor(
    private val repository: ExpenseRepository,
    private val application: Application
) : AndroidViewModel(application) {

    private val notificationHelper = NotificationHelper(application.applicationContext)
    private val largeExpenseThreshold = 5000.0 // ₹5000

    suspend fun addExpense(expenseEntity: ExpenseEntity): Boolean {
        return try {
            repository.insertExpense(expenseEntity)

            // Check if it's a large expense and show alert
            if (expenseEntity.type == "Expense" && expenseEntity.amount >= largeExpenseThreshold) {
                notificationHelper.showLargeExpenseAlert(
                    expenseEntity.amount,
                    expenseEntity.title
                )
            }

            true
        } catch (ex: Throwable) {
            false
        }
    }
}