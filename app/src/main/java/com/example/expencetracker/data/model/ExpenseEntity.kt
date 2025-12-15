package com.example.expencetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Enumeration

@Entity(tableName = "expense_table")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String,
    val amount: Double,
    val date: String,
    val category: String,
    val type: String,
)

@Entity(tableName = "budget_table")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val monthlyBudget: Double,
    val userId: String = "default",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "category_budget_table")
data class CategoryBudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val category: String,
    val budgetAmount: Double,
    val userId: String = "default"
)

@Entity(tableName = "recurring_transaction_table")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String,
    val amount: Double,
    val category: String,
    val type: String,
    val frequency: String,
    val startDate: Long,
    val isActive: Boolean = true,
    val lastExecuted: Long = 0L
)
