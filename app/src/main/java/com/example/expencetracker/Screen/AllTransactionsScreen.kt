package com.example.expencetracker.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expencetracker.R
import com.example.expencetracker.Utils
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.viewmodel.HomeVM
import kotlinx.coroutines.launch

// Premium Color Palette
private val PrimaryBlue = Color(0xFF5B8DEE)
private val DarkBlue = Color(0xFF4A7BD9)
private val LightBlue = Color(0xFFE8F1FF)
private val AccentGreen = Color(0xFF00C9A7)
private val AccentOrange = Color(0xFFFF9671)
private val AccentRed = Color(0xFFFF6B6B)
private val BackgroundLight = Color(0xFFF8F9FD)
private val CardBackground = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A2138)
private val TextGray = Color(0xFF8F92A1)

@Composable
fun AllTransactionsScreen(navController: NavController) {
    val viewModel: HomeVM = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }
    var selectedFilter by remember { mutableStateOf(0) } // 0=All, 1=Income, 2=Expense

    val state = viewModel.expenses.collectAsState(initial = emptyList())
    val filters = listOf("All", "Income", "Expense")

    // Filter transactions
    val filteredTransactions = when (selectedFilter) {
        1 -> state.value.filter { it.type == "Income" }
        2 -> state.value.filter { it.type == "Expense" }
        else -> state.value
    }

    // Calculate totals
    val totalIncome = filteredTransactions.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpense = filteredTransactions.filter { it.type == "Expense" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

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
                        text = "All Transactions",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.size(44.dp))
                }
            }

            if (state.value.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(text = "📝", fontSize = 80.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "No Transactions Yet",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Start tracking your income and expenses",
                            fontSize = 15.sp,
                            color = TextGray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    // Summary Card (Overlapping)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-50).dp)
                                .padding(horizontal = 24.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackground)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "Total Transactions",
                                                fontSize = 13.sp,
                                                color = TextGray
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${filteredTransactions.size}",
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextDark
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "Balance",
                                                fontSize = 13.sp,
                                                color = TextGray
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "₹${String.format("%,.0f", balance)}",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (balance >= 0) AccentGreen else AccentRed
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Income
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(AccentGreen.copy(alpha = 0.1f))
                                                .padding(12.dp)
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Income",
                                                    fontSize = 11.sp,
                                                    color = TextGray
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "₹${
                                                        String.format(
                                                            "%,.0f",
                                                            totalIncome
                                                        )
                                                    }",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AccentGreen
                                                )
                                            }
                                        }

                                        // Expense
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(AccentOrange.copy(alpha = 0.1f))
                                                .padding(12.dp)
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Expense",
                                                    fontSize = 11.sp,
                                                    color = TextGray
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "₹${
                                                        String.format(
                                                            "%,.0f",
                                                            totalExpense
                                                        )
                                                    }",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AccentOrange
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Filter Tabs
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackground),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    filters.forEachIndexed { index, title ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    when {
                                                        selectedFilter == index && index == 0 -> PrimaryBlue
                                                        selectedFilter == index && index == 1 -> AccentGreen
                                                        selectedFilter == index && index == 2 -> AccentOrange
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .clickable { selectedFilter = index }
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = title,
                                                fontSize = 14.sp,
                                                fontWeight = if (selectedFilter == index) FontWeight.Bold else FontWeight.Normal,
                                                color = if (selectedFilter == index) Color.White else TextGray
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Transactions
                    if (filteredTransactions.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .height(160.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackground),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = when (selectedFilter) {
                                                1 -> "💰"
                                                2 -> "💸"
                                                else -> "📊"
                                            },
                                            fontSize = 48.sp
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "No ${filters[selectedFilter].lowercase()} found",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDark
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        items(filteredTransactions) { item ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    when (dismissValue) {
                                        SwipeToDismissBoxValue.EndToStart -> {
                                            itemToDelete = item
                                            showDeleteDialog = true
                                            false
                                        }

                                        else -> false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                backgroundContent = {
                                    val backgroundColor = when (dismissState.dismissDirection) {
                                        SwipeToDismissBoxValue.EndToStart -> AccentRed
                                        else -> Color.Transparent
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(backgroundColor)
                                            .padding(horizontal = 24.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        if (backgroundColor != Color.Transparent) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color.White,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            ) {
                                TransactionCard(item = item)
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Dialog
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                itemToDelete = null
            },
            title = {
                Text(
                    text = "Delete Transaction",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextDark
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this transaction?",
                    fontSize = 15.sp,
                    color = TextGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { expense ->
                            coroutineScope.launch {
                                viewModel.deleteExpense(expense)
                            }
                            Toast.makeText(
                                context,
                                "Transaction deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        showDeleteDialog = false
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", modifier = Modifier.padding(horizontal = 16.dp))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    itemToDelete = null
                }) {
                    Text("Cancel", color = TextGray)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun TransactionCard(item: ExpenseEntity) {
    val icon = Utils.getItemIcon(item)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (item.type == "Income")
                            AccentGreen.copy(alpha = 0.15f)
                        else
                            AccentOrange.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.category} • ${item.date}",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            Text(
                text = if (item.type == "Income") "+₹${item.amount.toInt()}" else "-₹${item.amount.toInt()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.type == "Income") AccentGreen else AccentOrange
            )
        }
    }
}
