package com.example.expencetracker.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.expencetracker.R
import com.example.expencetracker.Utils
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.viewmodel.AddExpenseVM
import com.example.expencetracker.widget.ExpenceTextView
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions

// Premium Color Palette (matching HomeScreen)
private val PrimaryBlue = Color(0xFF5B8DEE)
private val DarkBlue = Color(0xFF4A7BD9)
private val LightBlue = Color(0xFFE8F1FF)
private val AccentGreen = Color(0xFF00C9A7)
private val AccentOrange = Color(0xFFFF9671)
private val BackgroundLight = Color(0xFFF8F9FD)
private val CardBackground = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A2138)
private val TextGray = Color(0xFF8F92A1)

@Composable
fun AddExpence(navController: NavController) {
    val viewmodel: AddExpenseVM = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Get theme colors
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(0L) }
    var showDatePicker by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") } // Default to Expense
    var showCategoryDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Premium Header with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(PrimaryBlue, DarkBlue),
                            startY = 0f,
                            endY = 400f
                        )
                    )
                    .padding(horizontal = 24.dp)
                    .padding(top = 50.dp, bottom = 30.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back Button
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Title
                    Text(
                        text = "Add Transaction",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Spacer for balance
                    Spacer(modifier = Modifier.size(44.dp))
                }
            }

            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Income/Expense Toggle
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Income Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (type == "Income") AccentGreen else Color.Transparent
                                )
                                .clickable { type = "Income" }
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "💰 Income",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (type == "Income") Color.White else TextGray
                            )
                        }

                        // Expense Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (type == "Expense") AccentOrange else Color.Transparent
                                )
                                .clickable { type = "Expense" }
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "💸 Expense",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (type == "Expense") Color.White else TextGray
                            )
                        }
                    }
                }

                // Amount Input
                Text(
                    text = "Amount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it.filter { char -> char.isDigit() || char == '.' }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    placeholder = { Text("0", color = textSecondary) },
                    leadingIcon = {
                        Text(
                            "₹",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (type == "Income") AccentGreen else AccentOrange
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (type == "Income") AccentGreen else AccentOrange,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor
                    )
                )

                // Title/Name Input
                Text(
                    text = "Title",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    placeholder = { Text("e.g., Lunch, Salary, etc.", color = textSecondary) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor
                    )
                )

                // Category Selection
                Text(
                    text = "Category",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = if (category.isEmpty()) "" else category,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryDialog = true }
                        .padding(bottom = 20.dp),
                    placeholder = { Text("Select Category", color = textSecondary) },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = textSecondary
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor,
                        disabledTextColor = textColor,
                        disabledBorderColor = Color(0xFFE0E0E0)
                    ),
                    enabled = false
                )

                // Date Selection
                Text(
                    text = "Date",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = if (date == 0L) "" else Utils.formatDateToHumanReadableForm(date),
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(bottom = 32.dp),
                    placeholder = { Text("Select Date", color = textSecondary) },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor,
                        disabledTextColor = textColor,
                        disabledBorderColor = Color(0xFFE0E0E0)
                    ),
                    enabled = false
                )

                // Add Button
                Button(
                    onClick = {
                        // Validation
                        when {
                            amount.isEmpty() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0 -> {
                                Toast.makeText(
                                    context,
                                    "Please enter a valid amount",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            name.isEmpty() -> {
                                Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            category.isEmpty() -> {
                                Toast.makeText(
                                    context,
                                    "Please select a category",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            date == 0L -> {
                                Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            else -> {
                                val model = ExpenseEntity(
                                    null,
                                    name,
                                    amount = amount.toDouble(),
                                    Utils.formatDateToHumanReadableForm(date),
                                    category = category,
                                    type = type
                                )
                                coroutineScope.launch {
                                    if (viewmodel.addExpense(model)) {
                                        Toast.makeText(
                                            context,
                                            "${if (type == "Income") "Income" else "Expense"} added successfully! 🎉",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == "Income") AccentGreen else PrimaryBlue
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Add ${type}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        ExpenseDatePickerDialog(
            onDateSelected = {
                date = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // Category Dialog
    if (showCategoryDialog) {
        CategorySelectionDialog(
            type = type,
            onCategorySelected = {
                category = it
                showCategoryDialog = false
            },
            onDismiss = { showCategoryDialog = false }
        )
    }
}

@Composable
fun CategorySelectionDialog(
    type: String,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val expenseCategories = listOf(
        "🍔 Food & Dining" to "Food",
        "🚗 Transport" to "Transport",
        "🛒 Shopping" to "Shopping",
        "🎬 Entertainment" to "Entertainment",
        "🏥 Health" to "Health",
        "📚 Education" to "Education",
        "💡 Bills & Utilities" to "Bills",
        "🏠 Rent & Housing" to "Rent",
        "💪 Fitness & Sports" to "Gym",
        "🎁 Gifts" to "Gifts",
        "🍕 Restaurant" to "Restaurant",
        "☕ Starbucks" to "Starbucks",
        "📺 Netflix" to "Netflix",
        "💳 Paypal" to "Paypal",
        "💰 GPay" to "GPay",
        "🎮 Youtube" to "Youtube",
        "🚕 Uber/Ola" to "Uber",
        "🛍️ Groceries" to "Groceries",
        "💊 Pharmacy" to "Pharmacy",
        "⚡ Electricity" to "Electricity",
        "📱 Other" to "Other"
    )

    val incomeCategories = listOf(
        "💼 Salary" to "Salary",
        "💻 Freelance" to "Freelance",
        "🏢 Business" to "Business",
        "📈 Investment" to "Investment",
        "🎯 Bonus" to "Bonus",
        "💰 Upwork" to "Upwork",
        "💵 Other Income" to "Income"
    )

    val categories = if (type == "Income") incomeCategories else expenseCategories

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Category",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                categories.forEach { (displayName, value) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onCategorySelected(value) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.3f
                            )
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Text(
                            text = displayName,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun AddExpencePreview() {
    AddExpence(rememberNavController())
}