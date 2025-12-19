package com.example.expencetracker.Screen

import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expencetracker.R
import com.example.expencetracker.Utils
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.viewmodel.StashVM
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun StashScreen(navController: NavController) {
    val viewModel: StashVM = hiltViewModel()
    var selectedTab by remember { mutableStateOf(0) } // 0=Overview, 1=Income, 2=Expense
    var selectedPeriod by remember { mutableStateOf(1) } // 0=Day, 1=Month, 2=Year

    // Get theme colors
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val incomeColor = MaterialTheme.colorScheme.tertiary
    val expenseColor = MaterialTheme.colorScheme.error

    val tabs = listOf("Overview", "Income", "Expense")
    val periods = listOf("Day", "Month", "Year")

    val dataState = viewModel.entries.collectAsState(emptyList())
    val topExpense = viewModel.topEntries.collectAsState(initial = emptyList())
    val topIncome = viewModel.topIncome.collectAsState(initial = emptyList())
    val incomeChartData = viewModel.incomeChartEntries.collectAsState(initial = emptyList())

    // Calculate totals
    val totalIncome = topIncome.value.sumOf { it.amount }
    val totalExpense = topExpense.value.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Premium Header - Fixed at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(primaryColor, primaryContainer),
                            startY = 0f,
                            endY = 400f
                        )
                    )
                    .padding(horizontal = 24.dp)
                    .padding(top = 50.dp, bottom = 20.dp)
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
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Statistics",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.size(44.dp))
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp, top = 16.dp)
            ) {
                // Summary Cards - Now properly below header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        // Balance Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            ) {
                                Text(
                                    text = "Total Balance",
                                    fontSize = 14.sp,
                                    color = textSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "₹ ${String.format("%,.0f", balance)}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Income
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(incomeColor.copy(alpha = 0.1f))
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(incomeColor),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowDownward,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onTertiary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = "Income",
                                                    fontSize = 11.sp,
                                                    color = textSecondary
                                                )
                                                Text(
                                                    text = "₹${
                                                        String.format(
                                                            "%,.0f",
                                                            totalIncome
                                                        )
                                                    }",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = incomeColor
                                                )
                                            }
                                        }
                                    }

                                    // Expense
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(expenseColor.copy(alpha = 0.1f))
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(expenseColor),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowUpward,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onError,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = "Expense",
                                                    fontSize = 11.sp,
                                                    color = textSecondary
                                                )
                                                Text(
                                                    text = "₹${
                                                        String.format(
                                                            "%,.0f",
                                                            totalExpense
                                                        )
                                                    }",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = expenseColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tab Selector
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(2.dp)
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
                                                when {
                                                    selectedTab == index && index == 0 -> primaryColor
                                                    selectedTab == index && index == 1 -> incomeColor
                                                    selectedTab == index && index == 2 -> expenseColor
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable { selectedTab = index }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = title,
                                            fontSize = 14.sp,
                                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedTab == index) MaterialTheme.colorScheme.onPrimary else textSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Chart Section
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        val chartEntries = when (selectedTab) {
                            1 -> viewModel.getTopEntriesForIncome(incomeChartData.value)
                            2 -> viewModel.getEntriesForChart(dataState.value)
                            else -> viewModel.getEntriesForChart(dataState.value)
                        }

                        ModernLineChart(
                            entries = chartEntries,
                            type = when (selectedTab) {
                                1 -> "Income"
                                2 -> "Expense"
                                else -> "Overview"
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Top Transactions
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (selectedTab) {
                                    1 -> "Top Income"
                                    2 -> "Top Expenses"
                                    else -> "Recent Transactions"
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (selectedTab == 1) incomeColor else expenseColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Transaction List
                val dataToShow: List<ExpenseEntity> = when (selectedTab) {
                    1 -> topIncome.value
                    2 -> topExpense.value
                    else -> topExpense.value.take(5) + topIncome.value.take(5)
                }

                if (dataToShow.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(160.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = when (selectedTab) {
                                        1 -> "💰"
                                        2 -> "💸"
                                        else -> "📊"
                                    },
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No data available",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = when (selectedTab) {
                                        1 -> "Add some income entries"
                                        2 -> "Add some expense entries"
                                        else -> "Start tracking your finances"
                                    },
                                    fontSize = 14.sp,
                                    color = textSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(dataToShow.take(5)) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = Utils.getItemIcon(item)

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (item.type == "Income")
                                                incomeColor.copy(alpha = 0.15f)
                                            else
                                                expenseColor.copy(alpha = 0.15f)
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
                                        color = textColor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${item.category} • ${item.date}",
                                        fontSize = 12.sp,
                                        color = textSecondary
                                    )
                                }

                                Text(
                                    text = if (item.type == "Income") "+₹${item.amount.toInt()}" else "-₹${item.amount.toInt()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.type == "Income") incomeColor else expenseColor
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ModernLineChart(entries: List<Entry>, type: String) {
    val context = LocalContext.current

    if (entries.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📊", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Data Yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start adding transactions\nto see beautiful insights",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        return
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = when (type) {
                            "Income" -> "💰 Income Trend"
                            "Expense" -> "💸 Spending Trend"
                            else -> "📊 Financial Overview"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Last ${entries.size} transactions",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (type) {
                            "Income" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                            "Expense" -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        }
                    )
                ) {
                    Text(
                        text = "₹${entries.sumOf { it.y.toDouble() }.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (type) {
                            "Income" -> MaterialTheme.colorScheme.tertiary
                            "Expense" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chart
            AndroidView(
                factory = {
                    val view = LayoutInflater.from(context).inflate(R.layout.stash_line_chart, null)
                    view
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) { view ->
                val lineChart = view.findViewById<LineChart>(R.id.lineChart)

                val chartColor = when (type) {
                    "Income" -> android.graphics.Color.parseColor("#00C9A7")
                    "Expense" -> android.graphics.Color.parseColor("#FF9671")
                    else -> android.graphics.Color.parseColor("#5B8DEE")
                }

                val dataSet = LineDataSet(entries, "").apply {
                    color = chartColor
                    lineWidth = 3f
                    setCircleColor(chartColor)
                    circleRadius = 5f
                    circleHoleRadius = 2.5f
                    setDrawCircleHole(true)
                    setDrawFilled(true)
                    fillAlpha = 30

                    val drawable = when (type) {
                        "Income" -> ContextCompat.getDrawable(
                            context,
                            R.drawable.income_chart_gradient
                        )

                        else -> ContextCompat.getDrawable(context, R.drawable.chart_gradient)
                    }
                    drawable?.let { fillDrawable = it }

                    valueTextSize = 10f
                    valueTextColor = chartColor
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.2f
                }

                lineChart.apply {
                    data = LineData(dataSet)
                    description.isEnabled = false
                    legend.isEnabled = false

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        textColor = android.graphics.Color.parseColor("#8F92A1")
                        textSize = 10f
                        granularity = 1f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return Utils.formatDateForCHart(value.toLong())
                            }
                        }
                    }

                    axisLeft.isEnabled = false
                    axisRight.apply {
                        isEnabled = true
                        setDrawGridLines(true)
                        gridColor = android.graphics.Color.parseColor("#F5F5F5")
                        textColor = android.graphics.Color.parseColor("#8F92A1")
                        textSize = 10f
                    }

                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(false)
                    animateXY(1000, 1000)
                    invalidate()
                }
            }
        }
    }
}