package com.example.expencetracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)

        when (intent.action) {
            "com.example.expencetracker.DAILY_REMINDER" -> {
                notificationHelper.showDailyReminder()
            }

            "com.example.expencetracker.BUDGET_ALERT" -> {
                val percentage = intent.getIntExtra("percentage", 0)
                val spent = intent.getDoubleExtra("spent", 0.0)
                val budget = intent.getDoubleExtra("budget", 0.0)
                if (percentage > 0) {
                    notificationHelper.showBudgetAlert(percentage, spent, budget)
                }
            }

            "com.example.expencetracker.MONTHLY_REPORT" -> {
                val totalIncome = intent.getDoubleExtra("totalIncome", 0.0)
                val totalExpense = intent.getDoubleExtra("totalExpense", 0.0)
                val balance = intent.getDoubleExtra("balance", 0.0)
                notificationHelper.showMonthlyReport(totalExpense, totalIncome, balance)
            }
        }
    }
}
