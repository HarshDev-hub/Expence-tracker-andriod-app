package com.example.expencetracker.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.expencetracker.MainActivity
import com.example.expencetracker.R
import java.util.Calendar

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "expense_tracker_channel"
        const val CHANNEL_NAME = "Expense Tracker"
        const val NOTIFICATION_ID_DAILY = 1001
        const val NOTIFICATION_ID_BUDGET = 1002
        const val NOTIFICATION_ID_MONTHLY = 1003
        const val NOTIFICATION_ID_LARGE = 1004
        
        // SharedPreferences keys
        private const val PREFS_NAME = "notification_prefs"
        private const val KEY_LAST_ALERT_PERCENTAGE = "last_alert_percentage"
        private const val KEY_LAST_ALERT_DATE = "last_alert_date"
        private const val KEY_DAILY_REMINDER_SCHEDULED = "daily_reminder_scheduled"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Expense tracker notifications"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showDailyReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("💰 Track Your Spending")
            .setContentText("Don't forget to add today's expenses!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (checkNotificationPermission()) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DAILY, notification)
        }
    }

    @SuppressLint("DefaultLocale")
    fun showBudgetAlert(percentage: Int, spent: Double, budget: Double) {
        // Check if we should show this alert (avoid duplicates)
        if (!shouldShowBudgetAlert(percentage)) {
            return
        }

        val title = when {
            percentage >= 100 -> "⚠️ Budget Exceeded!"
            percentage >= 90 -> "🚨 Budget Alert: 90% Used"
            percentage >= 80 -> "⚡ Budget Warning: 80% Used"
            else -> "📊 Budget Update"
        }

        val message = "You've spent ₹${String.format("%,.0f", spent)} of ₹${
            String.format(
                "%,.0f",
                budget
            )
        } ($percentage%)"

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        if (checkNotificationPermission()) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BUDGET, notification)
            // Save alert state
            saveLastAlertState(percentage)
        }
    }

    fun showLargeExpenseAlert(amount: Double, title: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("💸 Large Expense Alert")
            .setContentText("$title - ₹${String.format("%,.0f", amount)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (checkNotificationPermission()) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_LARGE, notification)
        }
    }

    fun showMonthlyReport(totalExpense: Double, totalIncome: Double, balance: Double) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val message = "Income: ₹${String.format("%,.0f", totalIncome)}\n" +
                "Expense: ₹${String.format("%,.0f", totalExpense)}\n" +
                "Balance: ₹${String.format("%,.0f", balance)}"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("📊 Monthly Financial Report")
            .setContentText("Your monthly summary is ready!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        if (checkNotificationPermission()) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_MONTHLY, notification)
        }
    }

    fun scheduleDailyReminder(hour: Int = 20, minute: Int = 0) {
        // Check if already scheduled to avoid duplicates
        if (isDailyReminderScheduled()) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.example.expencetracker.DAILY_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            100,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            // Mark as scheduled
            prefs.edit().putBoolean(KEY_DAILY_REMINDER_SCHEDULED, true).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scheduleMonthlyReport() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.example.expencetracker.MONTHLY_REPORT"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            200,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.MONTH, 1)
            }
        }

        try {
            // Schedule to repeat every month
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * 30, // Approximate month
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelDailyReminder() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            100,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            prefs.edit().putBoolean(KEY_DAILY_REMINDER_SCHEDULED, false).apply()
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun requestNotificationPermission(): Boolean {
        return checkNotificationPermission()
    }

    // Helper methods for alert tracking
    
    private fun shouldShowBudgetAlert(percentage: Int): Boolean {
        val lastPercentage = prefs.getInt(KEY_LAST_ALERT_PERCENTAGE, 0)
        val today = getCurrentDate()
        val lastAlertDate = prefs.getString(KEY_LAST_ALERT_DATE, "")
        
        // Reset alerts on new day
        if (lastAlertDate != today) {
            resetAlertState()
            return true
        }
        
        // Only show if percentage is higher threshold
        return when {
            percentage >= 100 && lastPercentage < 100 -> true
            percentage >= 90 && lastPercentage < 90 -> true
            percentage >= 80 && lastPercentage < 80 -> true
            else -> false
        }
    }
    
    private fun saveLastAlertState(percentage: Int) {
        prefs.edit().apply {
            putInt(KEY_LAST_ALERT_PERCENTAGE, percentage)
            putString(KEY_LAST_ALERT_DATE, getCurrentDate())
            apply()
        }
    }
    
    fun resetAlertState() {
        prefs.edit().apply {
            putInt(KEY_LAST_ALERT_PERCENTAGE, 0)
            putString(KEY_LAST_ALERT_DATE, "")
            apply()
        }
    }
    
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }
    
    private fun isDailyReminderScheduled(): Boolean {
        return prefs.getBoolean(KEY_DAILY_REMINDER_SCHEDULED, false)
    }
}
