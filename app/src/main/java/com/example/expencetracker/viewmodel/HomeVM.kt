package com.example.expencetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expencetracker.R
import com.example.expencetracker.Utils
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses = repository.getAllExpenses()

    fun getBalance(list: List<ExpenseEntity>): String {
        var balance = 0.0
        for (expense in list) {
            if (expense.type == "Income") {
                balance += expense.amount
            } else {
                balance -= expense.amount
            }
        }
        return "$ ${Utils.formatToDecimalValue(balance)}"
    }

    fun getTotalExpense(list: List<ExpenseEntity>): String {
        var total = 0.0
        for (expense in list) {
            if (expense.type != "Income") {
                total += expense.amount
            }
        }
        return "$ ${Utils.formatToDecimalValue(total)}"
    }

    fun getTotalIncome(list: List<ExpenseEntity>): String {
        var totalIncome = 0.0
        for (expense in list) {
            if (expense.type == "Income") {
                totalIncome += expense.amount
            }
        }
        return "$ ${Utils.formatToDecimalValue(totalIncome)}"
    }

    fun deleteExpense(expenseEntity: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expenseEntity)
        }
    }
}