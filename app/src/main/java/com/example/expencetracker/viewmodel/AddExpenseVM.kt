package com.example.expencetracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expencetracker.data.ExpenseDatabase
import com.example.expencetracker.data.dao.ExpenseDao
import com.example.expencetracker.data.model.ExpenseEntity

class AddExpenseVM(val dao: ExpenseDao):ViewModel() {

    suspend fun addExpense(expenseEntity: ExpenseEntity): Boolean{
       try {
           dao.insertExpense(expenseEntity)
           return true
       }catch (ex: Throwable){
           return false
       }
    }

}

class AddExpenseVMFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddExpenseVM::class.java)) {
            val dao = ExpenseDatabase.getDatabase(context).expenseDao()
            @Suppress("UNCHECKED_CAST")
            return AddExpenseVM(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}