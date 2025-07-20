package com.example.expencetracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expencetracker.Utils
import com.example.expencetracker.data.ExpenseDatabase
import com.example.expencetracker.data.dao.ExpenseDao
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.data.model.ExpenseSummary
import com.github.mikephil.charting.data.Entry

class StashVM(val dao: ExpenseDao):ViewModel() {
    val entries = dao.getAllExpenseByDate()
    val topEntries = dao.getTopExpense()
    fun getEntriesForChart(entries: List<ExpenseSummary>):List<Entry>{
        val list = mutableListOf<Entry>()
        for(entry in entries){
            val formattedDate = Utils.getMillisFromDate(entry.date)
            list.add(Entry(formattedDate.toFloat(),entry.total_amount.toFloat()))
        }
        return list

    }
}
class StashVMFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StashVM::class.java)) {
            val dao = ExpenseDatabase.getDatabase(context).expenseDao()
            @Suppress("UNCHECKED_CAST")
            return StashVM(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}