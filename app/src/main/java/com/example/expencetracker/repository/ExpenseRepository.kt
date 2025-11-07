package com.example.expencetracker.repository

import com.example.expencetracker.data.dao.ExpenseDao
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.data.model.ExpenseSummary
import com.example.expencetracker.data.model.IncomeSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    // Get all expenses
    fun getAllExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    // Get top expenses
    fun getTopExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getTopExpense()

    // Get top income
    fun getTopIncome(): Flow<List<ExpenseEntity>> = expenseDao.getTopIncome()

    // Get expenses by date for chart
    fun getExpensesByDate(type: String = "Expense"): Flow<List<ExpenseSummary>> =
        expenseDao.getAllExpenseByDate(type)

    // Get income entries for chart
    fun getIncomeEntries(type: String = "Income"): Flow<List<IncomeSummary>> =
        expenseDao.getTopEntriesForIncome(type)

    // Insert expense
    suspend fun insertExpense(expense: ExpenseEntity) = expenseDao.insertExpense(expense)

    // Delete expense
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteExpense(expense)

    // Update expense
    suspend fun updateExpense(expense: ExpenseEntity) = expenseDao.updateExpense(expense)
}