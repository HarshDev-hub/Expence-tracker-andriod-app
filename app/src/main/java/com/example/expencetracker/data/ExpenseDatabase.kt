package com.example.expencetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.expencetracker.data.dao.BudgetDao
import com.example.expencetracker.data.dao.ExpenseDao
import com.example.expencetracker.data.model.BudgetEntity
import com.example.expencetracker.data.model.CategoryBudgetEntity
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.data.model.RecurringTransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExpenseEntity::class,
        BudgetEntity::class,
        CategoryBudgetEntity::class,
        RecurringTransactionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        const val DATABASE_NAME = "expense_db"

        // Migration from version 1 to 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS budget_table (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        monthlyBudget REAL NOT NULL,
                        userId TEXT NOT NULL DEFAULT 'default',
                        lastUpdated INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        // Migration from version 2 to 3
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `category_budget_table` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                        `category` TEXT NOT NULL,
                        `budgetAmount` REAL NOT NULL,
                        `userId` TEXT NOT NULL DEFAULT 'default'
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `recurring_transaction_table` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                        `title` TEXT NOT NULL,
                        `amount` REAL NOT NULL,
                        `category` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        `frequency` TEXT NOT NULL,
                        `startDate` INTEGER NOT NULL,
                        `isActive` INTEGER NOT NULL DEFAULT 1,
                        `lastExecuted` INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
            }
        }

        @JvmStatic
        fun getDatabase(context: Context): ExpenseDatabase {
            return Room.databaseBuilder(
                context,
                ExpenseDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    InitBasicData(context)
                }
                fun InitBasicData(context: Context) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = getDatabase(context).expenseDao()
                        val budgetDao = getDatabase(context).budgetDao()
                        // Insert default budget
                        budgetDao.insertBudget(
                            BudgetEntity(
                                id = null,
                                monthlyBudget = 50000.0,
                                userId = "default"
                            )
                        )
                    }

                }
            }).build()
        }
    }
}