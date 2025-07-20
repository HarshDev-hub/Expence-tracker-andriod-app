package com.example.expencetracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expencetracker.R
import com.example.expencetracker.Utils
import com.example.expencetracker.data.ExpenseDatabase
import com.example.expencetracker.data.dao.ExpenseDao
import com.example.expencetracker.data.model.ExpenseEntity

class HomeVM(dao: ExpenseDao):ViewModel() {
    val expenses = dao.getAllExpenses()

    fun getBalance(list: List<ExpenseEntity>): String{
        var balance = 0.0
        for (expenses in list) {
            if (expenses.type == "Income"){
                balance += expenses.amount
            }else{
                balance -= expenses.amount

            }
        }
        return "$ ${Utils.formatToDecimalValue(balance)}"
    }

    fun getTotalExpense(list: List<ExpenseEntity>): String{
        var total = 0.0
       for (expenses in list) {
           if(expenses.type != "Income") {
               total += expenses.amount
           }
        }
        return "$ ${Utils.formatToDecimalValue(total)}"
    }

    fun getTotalIncome(list: List<ExpenseEntity>):String{
        var totalIncome = 0.0
        for (expenses in list) {
            if (expenses.type == "Income"){
                totalIncome += expenses.amount
            }
        }
        return "$ ${Utils.formatToDecimalValue(totalIncome)}"
    }
}


class HomeVMFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeVM::class.java)) {
            val dao = ExpenseDatabase.getDatabase(context).expenseDao()
            @Suppress("UNCHECKED_CAST")
            return HomeVM(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}