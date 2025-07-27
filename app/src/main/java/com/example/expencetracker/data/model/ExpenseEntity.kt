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
