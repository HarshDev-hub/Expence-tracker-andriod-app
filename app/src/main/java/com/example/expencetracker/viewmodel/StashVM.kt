package com.example.expencetracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.expencetracker.Utils
import com.example.expencetracker.data.model.ExpenseSummary
import com.example.expencetracker.data.model.IncomeSummary
import com.example.expencetracker.repository.ExpenseRepository
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StashVM @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {
    val entries = repository.getExpensesByDate()
    val topEntries = repository.getTopExpenses()
    val topIncome = repository.getTopIncome()
    val incomeChartEntries = repository.getIncomeEntries()

    fun getEntriesForChart(entries: List<ExpenseSummary>): List<Entry> {
        val list = mutableListOf<Entry>()
        for (entry in entries) {
            val formattedDate = Utils.getMillisFromDate(entry.date)
            list.add(Entry(formattedDate.toFloat(), entry.total_amount.toFloat()))
        }
        return list
    }

    fun getTopEntriesForIncome(entries: List<IncomeSummary>): List<Entry> {
        val list = mutableListOf<Entry>()
        for (entry in entries) {
            val formattedDate = Utils.getMillisFromDate(entry.date)
            list.add(Entry(formattedDate.toFloat(), entry.total_amount.toFloat()))
        }
        return list
    }
}