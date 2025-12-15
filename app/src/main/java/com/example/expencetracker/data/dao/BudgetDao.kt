package com.example.expencetracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.expencetracker.data.model.BudgetEntity
import com.example.expencetracker.data.model.CategoryBudgetEntity
import com.example.expencetracker.data.model.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget_table WHERE userId = :userId LIMIT 1")
    fun getBudget(userId: String = "default"): Flow<BudgetEntity?>

    @Insert
    suspend fun insertBudget(budgetEntity: BudgetEntity)

    @Update
    suspend fun updateBudget(budgetEntity: BudgetEntity)

    @Query("DELETE FROM budget_table WHERE userId = :userId")
    suspend fun deleteBudget(userId: String = "default")

    // Category Budget
    @Query("SELECT * FROM category_budget_table WHERE userId = :userId")
    fun getAllCategoryBudgets(userId: String = "default"): Flow<List<CategoryBudgetEntity>>

    @Query("SELECT * FROM category_budget_table WHERE category = :category AND userId = :userId LIMIT 1")
    fun getCategoryBudget(category: String, userId: String = "default"): Flow<CategoryBudgetEntity?>

    @Insert
    suspend fun insertCategoryBudget(categoryBudgetEntity: CategoryBudgetEntity)

    @Update
    suspend fun updateCategoryBudget(categoryBudgetEntity: CategoryBudgetEntity)

    @Delete
    suspend fun deleteCategoryBudget(categoryBudgetEntity: CategoryBudgetEntity)

    // Recurring Transactions
    @Query("SELECT * FROM recurring_transaction_table WHERE isActive = 1")
    fun getActiveRecurringTransactions(): Flow<List<RecurringTransactionEntity>>

    @Query("SELECT * FROM recurring_transaction_table")
    fun getAllRecurringTransactions(): Flow<List<RecurringTransactionEntity>>

    @Insert
    suspend fun insertRecurringTransaction(recurringTransactionEntity: RecurringTransactionEntity)

    @Update
    suspend fun updateRecurringTransaction(recurringTransactionEntity: RecurringTransactionEntity)

    @Delete
    suspend fun deleteRecurringTransaction(recurringTransactionEntity: RecurringTransactionEntity)
}
