package com.example.expencetracker

import com.example.expencetracker.data.model.ExpenseEntity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utils {
    fun formatDateToHumanReadableForm(dateInMillis:Long): String{
        val dateForamatter = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
        return dateForamatter.format(dateInMillis)
    }

    fun formatDateForCHart(dateInMillis:Long): String{
        val dateForamatter = SimpleDateFormat("dd-MMM", Locale.getDefault())
        return dateForamatter.format(dateInMillis)
    }

    fun formatToDecimalValue(d:Double):String{
        return String.format("%.2f",d)

    }

    fun getMillisFromDate(date: String): Long {
        return getMilliFromDate(date)
    }

    fun getMilliFromDate(dateFormat: String?): Long {
        var date = Date()
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            date = formatter.parse(dateFormat)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        println("Today is $date")
        return date.time
    }

    fun getItemIcon(item: ExpenseEntity): Int {
        return when (item.category) {
            // Existing categories
            "Paypal", "PayPal" -> R.drawable.ic_paybal
            "Youtube", "YouTube" -> R.drawable.ic_yotube
            "Starbuck", "Starbucks" -> R.drawable.ic_starbuks
            "Netflix" -> R.drawable.ic_netflix
            "Upwork" -> R.drawable.ic_upwork
            "Gpay", "GPay", "Google Pay" -> R.drawable.ic_paybal

            // New Expense Categories
            "Food", "Restaurant", "Dining" -> R.drawable.ic_food
            "Transport", "Travel", "Uber", "Ola" -> R.drawable.ic_transport
            "Shopping", "Groceries" -> R.drawable.ic_shopping
            "Entertainment", "Movies", "Games" -> R.drawable.ic_entertainment
            "Health", "Medical", "Pharmacy" -> R.drawable.ic_health
            "Education", "Courses", "Books" -> R.drawable.ic_education
            "Bills", "Utilities", "Electricity" -> R.drawable.ic_bills
            "Rent", "Housing" -> R.drawable.ic_rent
            "Gym", "Fitness", "Sports" -> R.drawable.ic_gym
            "Gifts", "Donations" -> R.drawable.ic_gift

            // Income Categories
            "Salary", "Income" -> R.drawable.ic_salary
            "Freelance", "Business" -> R.drawable.ic_freelance
            "Investment", "Returns", "Dividend" -> R.drawable.ic_investment

            // Default
            else -> R.drawable.ic_other
        }
    }

    fun getGreetingMessage(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
}