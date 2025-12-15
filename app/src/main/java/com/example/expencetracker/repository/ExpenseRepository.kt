package com.example.expencetracker.repository

import android.content.Context
import com.example.expencetracker.data.ExpenseDatabase
import com.example.expencetracker.data.dao.BudgetDao
import com.example.expencetracker.data.dao.ExpenseDao
import com.example.expencetracker.data.model.BudgetEntity
import com.example.expencetracker.data.model.CategoryBudgetEntity
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.data.model.ExpenseSummary
import com.example.expencetracker.data.model.IncomeSummary
import com.example.expencetracker.data.model.RecurringTransactionEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao
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

    // Budget operations
    fun getBudget(userId: String = "default"): Flow<BudgetEntity?> = budgetDao.getBudget(userId)

    suspend fun insertBudget(budgetEntity: BudgetEntity) = budgetDao.insertBudget(budgetEntity)

    suspend fun updateBudget(budgetEntity: BudgetEntity) = budgetDao.updateBudget(budgetEntity)

    suspend fun deleteBudget(userId: String = "default") = budgetDao.deleteBudget(userId)

    // Category Budget operations
    fun getAllCategoryBudgets(userId: String = "default"): Flow<List<CategoryBudgetEntity>> =
        budgetDao.getAllCategoryBudgets(userId)

    fun getCategoryBudget(
        category: String,
        userId: String = "default"
    ): Flow<CategoryBudgetEntity?> =
        budgetDao.getCategoryBudget(category, userId)

    suspend fun insertCategoryBudget(categoryBudget: CategoryBudgetEntity) =
        budgetDao.insertCategoryBudget(categoryBudget)

    suspend fun updateCategoryBudget(categoryBudget: CategoryBudgetEntity) =
        budgetDao.updateCategoryBudget(categoryBudget)

    suspend fun deleteCategoryBudget(categoryBudget: CategoryBudgetEntity) =
        budgetDao.deleteCategoryBudget(categoryBudget)

    // Recurring Transaction operations
    fun getActiveRecurringTransactions(): Flow<List<RecurringTransactionEntity>> =
        budgetDao.getActiveRecurringTransactions()

    fun getAllRecurringTransactions(): Flow<List<RecurringTransactionEntity>> =
        budgetDao.getAllRecurringTransactions()

    suspend fun insertRecurringTransaction(recurringTransaction: RecurringTransactionEntity) =
        budgetDao.insertRecurringTransaction(recurringTransaction)

    suspend fun updateRecurringTransaction(recurringTransaction: RecurringTransactionEntity) =
        budgetDao.updateRecurringTransaction(recurringTransaction)

    suspend fun deleteRecurringTransaction(recurringTransaction: RecurringTransactionEntity) =
        budgetDao.deleteRecurringTransaction(recurringTransaction)
}