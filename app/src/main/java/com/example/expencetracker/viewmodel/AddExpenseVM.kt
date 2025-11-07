package com.example.expencetracker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddExpenseVM @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    suspend fun addExpense(expenseEntity: ExpenseEntity): Boolean {
        return try {
            repository.insertExpense(expenseEntity)
            true
        } catch (ex: Throwable) {
            false
        }
    }
}