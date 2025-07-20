package com.example.expencetracker

import com.example.expencetracker.data.model.ExpenseEntity
import java.text.ParseException
import java.text.SimpleDateFormat
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
        if(item.category == "Paypal"){
            return R.drawable.ic_paybal
        }else if(item.category == "Youtube"){
            return R.drawable.ic_yotube
        }else if(item.category == "Starbuck"){
            return R.drawable.ic_starbuks
        }else if(item.category == "Netflix"){
            return R.drawable.ic_netflix
        }
        else {
            return R.drawable.ic_upwork
        }
    }
}