package com.example.expencetracker.Screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expencetracker.data.model.CategoryBudgetEntity
import com.example.expencetracker.data.model.RecurringTransactionEntity
import com.example.expencetracker.viewmodel.HomeVM
import kotlinx.coroutines.launch

// Premium Color Palette
private val PrimaryBlue = Color(0xFF5B8DEE)
private val DarkBlue = Color(0xFF4A7BD9)
private val LightBlue = Color(0xFFE8F1FF)
private val AccentGreen = Color(0xFF00C9A7)
private val AccentOrange = Color(0xFFFF9671)
private val AccentRed = Color(0xFFFF6B6B)
private val AccentPurple = Color(0xFF9C27B0)
private val BackgroundLight = Color(0xFFF8F9FD)
private val CardBackground = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A2138)
private val TextGray = Color(0xFF8F92A1)

@Composable
fun BudgetManagementScreen(navController: NavController) {
    val viewModel: HomeVM = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) } // 0=Category Budgets, 1=Recurring
    var showAddDialog by remember { mutableStateOf(false) }

    val categoryBudgets by viewModel.categoryBudgets.collectAsState(initial = emptyList())
    val recurringTransactions by viewModel.recurringTransactions.collectAsState(initial = emptyList())

    val tabs = listOf("Category Budgets", "Recurring")

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundLight) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Premium Header
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

                    Text(
                        text = "Budget Settings",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White
                        )
                    }
                }
            }

            // Tab Selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selectedTab == index) PrimaryBlue else Color.Transparent
                                )
                                .clickable { selectedTab = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color.White else TextGray
                            )
                        }
                    }
                }
            }

            // Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                if (selectedTab == 0) {
                    // Category Budgets
                    if (categoryBudgets.isEmpty()) {
                        item {
                            EmptyStateCard(
                                emoji = "💰",
                                title = "No Category Budgets",
                                subtitle = "Set budgets for specific categories"
                            )
                        }
                    } else {
                        items(categoryBudgets) { budget ->
                            CategoryBudgetCard(
                                budget = budget,
                                onDelete = {
                                    coroutineScope.launch {
                                        viewModel.deleteCategoryBudget(budget)
                                        Toast.makeText(
                                            context,
                                            "Budget deleted",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    }
                } else {
                    // Recurring Transactions
                    if (recurringTransactions.isEmpty()) {
                        item {
                            EmptyStateCard(
                                emoji = "🔄",
                                title = "No Recurring Transactions",
                                subtitle = "Add automatic monthly expenses"
                            )
                        }
                    } else {
                        items(recurringTransactions) { transaction ->
                            RecurringTransactionCard(
                                transaction = transaction,
                                onToggle = {
                                    coroutineScope.launch {
                                        viewModel.updateRecurringTransaction(
                                            transaction.copy(isActive = !transaction.isActive)
                                        )
                                    }
                                },
                                onDelete = {
                                    coroutineScope.launch {
                                        viewModel.deleteRecurringTransaction(transaction)
                                        Toast.makeText(
                                            context,
                                            "Recurring transaction deleted",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        if (selectedTab == 0) {
            AddCategoryBudgetDialog(
                onDismiss = { showAddDialog = false },
                onSave = { category, amount ->
                    coroutineScope.launch {
                        viewModel.insertCategoryBudget(
                            CategoryBudgetEntity(
                                id = null,
                                category = category,
                                budgetAmount = amount
                            )
                        )
                        Toast.makeText(context, "Category budget added! 🎉", Toast.LENGTH_SHORT)
                            .show()
                    }
                    showAddDialog = false
                }
            )
        } else {
            AddRecurringTransactionDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, amount, category, type, frequency ->
                    coroutineScope.launch {
                        viewModel.insertRecurringTransaction(
                            RecurringTransactionEntity(
                                id = null,
                                title = title,
                                amount = amount,
                                category = category,
                                type = type,
                                frequency = frequency,
                                startDate = System.currentTimeMillis()
                            )
                        )
                        Toast.makeText(
                            context,
                            "Recurring transaction added! 🎉",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun CategoryBudgetCard(budget: CategoryBudgetEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💰", fontSize = 24.sp)
                }

                Column {
                    Text(
                        text = budget.category,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Budget: ₹${String.format("%,.0f", budget.budgetAmount)}",
                        fontSize = 14.sp,
                        color = AccentGreen
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = AccentRed
                )
            }
        }
    }
}

@Composable
fun RecurringTransactionCard(
    transaction: RecurringTransactionEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (transaction.type == "Income")
                                AccentGreen.copy(alpha = 0.15f)
                            else
                                AccentOrange.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔄", fontSize = 24.sp)
                }

                Column {
                    Text(
                        text = transaction.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "₹${transaction.amount.toInt()} • ${transaction.frequency}",
                        fontSize = 14.sp,
                        color = if (transaction.type == "Income") AccentGreen else AccentOrange
                    )
                    Text(
                        text = if (transaction.isActive) "Active" else "Paused",
                        fontSize = 12.sp,
                        color = if (transaction.isActive) AccentGreen else TextGray
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (transaction.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (transaction.isActive) "Pause" else "Resume",
                        tint = PrimaryBlue
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = AccentRed
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(emoji: String, title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = TextGray
            )
        }
    }
}

@Composable
fun AddCategoryBudgetDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit
) {
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var showCategoryPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val categories = listOf(
        "Food", "Transport", "Shopping", "Entertainment",
        "Health", "Education", "Bills", "Rent", "Gym", "Other"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Category Budget",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                // Category Selector
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryPicker = true },
                    label = { Text("Category") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, null)
                    },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = TextDark,
                        disabledBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { char -> char.isDigit() } },
                    label = { Text("Budget Amount") },
                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (category.isNotEmpty() && amount.isNotEmpty()) {
                        onSave(category, amount.toDouble())
                    } else {
                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showCategoryPicker) {
        AlertDialog(
            onDismissRequest = { showCategoryPicker = false },
            title = { Text("Select Category") },
            text = {
                LazyColumn {
                    items(categories) { cat ->
                        Text(
                            text = cat,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    category = cat
                                    showCategoryPicker = false
                                }
                                .padding(16.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun AddRecurringTransactionDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") }
    var frequency by remember { mutableStateOf("Monthly") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Recurring Transaction",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("e.g., Netflix Subscription") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { char -> char.isDigit() } },
                    label = { Text("Amount") },
                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = type == "Income",
                        onClick = { type = "Income" },
                        label = { Text("Income") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == "Expense",
                        onClick = { type = "Expense" },
                        label = { Text("Expense") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Frequency selector
                Text("Frequency:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Monthly", "Weekly", "Yearly").forEach { freq ->
                        FilterChip(
                            selected = frequency == freq,
                            onClick = { frequency = freq },
                            label = { Text(freq, fontSize = 12.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && amount.isNotEmpty() && category.isNotEmpty()) {
                        onSave(title, amount.toDouble(), category, type, frequency)
                    } else {
                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}