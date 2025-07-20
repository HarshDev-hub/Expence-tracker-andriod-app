package com.example.expencetracker.Screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDropDown(
    listOfItem:List<String>,
    onItemSelected:(item:String) -> Unit
){
    val expanded = remember {
        mutableStateOf(false)
    }
    val selectedItem = remember {
        mutableStateOf<String>(listOfItem[0])
    }
    // for dropdown
    ExposedDropdownMenuBox(expanded =expanded.value, onExpandedChange = {expanded.value = it}) {
        // to show te view
        TextField(value = selectedItem.value, onValueChange ={},
            modifier = Modifier.
            fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            }
            )
        ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = {}) {
            listOfItem.forEach {
                DropdownMenuItem(text = { Text(text = it) }, onClick = {
                    selectedItem.value = it
                    onItemSelected(selectedItem.value)
                expanded.value = false
                })
            }
        }
    }
}