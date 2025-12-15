package com.example.expencetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expencetracker.R
import com.example.expencetracker.Utils
import com.example.expencetracker.data.model.BudgetEntity
import com.example.expencetracker.data.model.CategoryBudgetEntity
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.data.model.RecurringTransactionEntity
import com.example.expencetracker.notification.NotificationHelper
import com.example.expencetracker.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val repository: ExpenseRepository,
    private val application: Application
) : AndroidViewModel(application) {

    val expenses = repository.getAllExpenses()
    val budget = repository.getBudget()
    val categoryBudgets = repository.getAllCategoryBudgets()
    val recurringTransactions = repository.getAllRecurringTransactions()

    private val _monthlyBudget = MutableStateFlow(50000.0)
    val monthlyBudget: StateFlow<Double> = _monthlyBudget

    private val notificationHelper = NotificationHelper(application.applicationContext)
    private var lastBudgetAlertPercentage = 0

    init {
        viewModelScope.launch {
            budget.collect { budgetEntity ->
                budgetEntity?.let {
                    _monthlyBudget.value = it.monthlyBudget
                }
            }
        }

        // Monitor expenses and budget for alerts
        viewModelScope.launch {
            combine(expenses, monthlyBudget) { expenseList, budget ->
                Pair(expenseList, budget)
            }.collect { (expenseList, budget) ->
                checkBudgetAlerts(expenseList, budget)
            }
        }

        // Schedule daily reminder
        notificationHelper.scheduleDailyReminder(20, 0) // 8 PM
    }

    private fun checkBudgetAlerts(expenses: List<ExpenseEntity>, budget: Double) {
        val totalExpense = expenses.filter { it.type == "Expense" }.sumOf { it.amount }
        val percentage = ((totalExpense / budget) * 100).toInt()

        // Show alert for 80%, 90%, 100% thresholds
        when {
            percentage >= 100 && lastBudgetAlertPercentage < 100 -> {
                notificationHelper.showBudgetAlert(percentage, totalExpense, budget)
                lastBudgetAlertPercentage = 100
            }

            percentage >= 90 && lastBudgetAlertPercentage < 90 -> {
                notificationHelper.showBudgetAlert(percentage, totalExpense, budget)
                lastBudgetAlertPercentage = 90
            }

            percentage >= 80 && lastBudgetAlertPercentage < 80 -> {
                notificationHelper.showBudgetAlert(percentage, totalExpense, budget)
                lastBudgetAlertPercentage = 80
            }
        }
    }

    fun getBalance(list: List<ExpenseEntity>): String {
        var balance = 0.0
        for (expense in list) {
            if (expense.type == "Income") {
                balance += expense.amount
            } else {
                balance -= expense.amount
            }
        }
        return "₹ ${Utils.formatToDecimalValue(balance)}"
    }

    fun getTotalExpense(list: List<ExpenseEntity>): String {
        var total = 0.0
        for (expense in list) {
            if (expense.type != "Income") {
                total += expense.amount
            }
        }
        return "₹ ${Utils.formatToDecimalValue(total)}"
    }

    fun getTotalIncome(list: List<ExpenseEntity>): String {
        var totalIncome = 0.0
        for (expense in list) {
            if (expense.type == "Income") {
                totalIncome += expense.amount
            }
        }
        return "₹ ${Utils.formatToDecimalValue(totalIncome)}"
    }

    fun deleteExpense(expenseEntity: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expenseEntity)
        }
    }

    fun updateBudget(newBudget: Double) {
        viewModelScope.launch {
            val budgetEntity = BudgetEntity(
                id = 1,
                monthlyBudget = newBudget,
                userId = "default",
                lastUpdated = System.currentTimeMillis()
            )
            try {
                repository.updateBudget(budgetEntity)
            } catch (e: Exception) {
                // If update fails, try insert
                repository.insertBudget(budgetEntity.copy(id = null))
            }
            _monthlyBudget.value = newBudget
            lastBudgetAlertPercentage = 0 // Reset alerts for new budget
        }
    }

    // Category Budget Methods

    fun insertCategoryBudget(categoryBudgetEntity: CategoryBudgetEntity) {
        viewModelScope.launch {
            repository.insertCategoryBudget(categoryBudgetEntity)
        }
    }

    fun deleteCategoryBudget(categoryBudgetEntity: CategoryBudgetEntity) {
        viewModelScope.launch {
            repository.deleteCategoryBudget(categoryBudgetEntity)
        }
    }

    // Recurring Transaction Methods

    fun insertRecurringTransaction(recurringTransactionEntity: RecurringTransactionEntity) {
        viewModelScope.launch {
            repository.insertRecurringTransaction(recurringTransactionEntity)
        }
    }

    fun updateRecurringTransaction(recurringTransactionEntity: RecurringTransactionEntity) {
        viewModelScope.launch {
            repository.updateRecurringTransaction(recurringTransactionEntity)
        }
    }

    fun deleteRecurringTransaction(recurringTransactionEntity: RecurringTransactionEntity) {
        viewModelScope.launch {
            repository.deleteRecurringTransaction(recurringTransactionEntity)
        }
    }
}