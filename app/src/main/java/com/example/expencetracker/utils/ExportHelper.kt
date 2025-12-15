package com.example.expencetracker.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.expencetracker.data.model.ExpenseEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object ExportHelper {

    fun exportToCSV(context: Context, transactions: List<ExpenseEntity>): File? {
        return try {
            val fileName = "ExpenseTracker_${
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            }.csv"

            // Use app's external files directory
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

            FileWriter(file).use { writer ->
                // CSV Header
                writer.append("Date,Title,Category,Type,Amount\n")

                // Data rows
                transactions.forEach { transaction ->
                    writer.append("${transaction.date},")
                    writer.append("${transaction.title},")
                    writer.append("${transaction.category},")
                    writer.append("${transaction.type},")
                    writer.append("${transaction.amount}\n")
                }
            }

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_SUBJECT, "Expense Tracker Report")
            putExtra(Intent.EXTRA_TEXT, "Here's my expense report from Expense Tracker app.")
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Report"))
    }

    fun generateMonthlyReport(transactions: List<ExpenseEntity>): String {
        val totalIncome = transactions.filter { it.type == "Income" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        val categoryExpenses = transactions
            .filter { it.type == "Expense" }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedByDescending { it.second }

        return buildString {
            appendLine("EXPENSE TRACKER - MONTHLY REPORT")
            appendLine("=".repeat(40))
            appendLine()
            appendLine("Total Income: ₹${String.format("%,.2f", totalIncome)}")
            appendLine("Total Expense: ₹${String.format("%,.2f", totalExpense)}")
            appendLine("Balance: ₹${String.format("%,.2f", balance)}")
            appendLine()
            appendLine("TOP CATEGORIES:")
            appendLine("-".repeat(40))
            categoryExpenses.take(5).forEach { (category, amount) ->
                appendLine("$category: ₹${String.format("%,.2f", amount)}")
            }
            appendLine()
            appendLine("Total Transactions: ${transactions.size}")
            appendLine(
                "Report generated on: ${
                    SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        Locale.getDefault()
                    ).format(Date())
                }"
            )
        }
    }
}