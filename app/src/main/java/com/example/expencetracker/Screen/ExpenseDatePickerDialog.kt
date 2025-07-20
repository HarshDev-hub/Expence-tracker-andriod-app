package com.example.expencetracker.Screen

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.example.expencetracker.widget.ExpenceTextView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDatePickerDialog(
    onDateSelected:(date:Long)->Unit,
    onDismiss: ()->Unit
){
    val datePickerState = rememberDatePickerState()// to store our date
    val selectedDate = datePickerState.selectedDateMillis ?: 0L
    DatePickerDialog(
        onDismissRequest = {onDismiss()},
        confirmButton = {
            TextButton(onClick = {onDateSelected(selectedDate)}) {
                ExpenceTextView(text = "Confirm")
        }
        },
        dismissButton = {
            TextButton(onClick = { onDateSelected(selectedDate) }) {
                ExpenceTextView(text = "Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }

}