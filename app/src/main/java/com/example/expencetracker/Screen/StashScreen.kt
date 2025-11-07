package com.example.expencetracker.Screen

import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.example.expencetracker.viewmodel.StashVM
import com.example.expencetracker.widget.ExpenceTextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun StashScreen(navController: NavController){
    var expanded by remember { mutableStateOf(false) } // for menu option
    var selectedOption by remember { mutableStateOf("TopExpense") } // for selected option to show according data
    Scaffold(topBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            ExpenceTextView(
                text = "Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier =  Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
            // menu button
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_dot),
                    contentDescription = null,
                    modifier = Modifier.size(21.dp),
                    colorFilter = ColorFilter.tint(Color.Black)
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded = false}
                ) {
                    DropdownMenuItem(
                        text = {
                            ExpenceTextView(
                                "Top Income",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.End
                            )
                        },
                        onClick = {
                            selectedOption = "TopIncome"
                            expanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            ExpenceTextView(
                                "Top Expense",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.End
                            )
                        },
                        onClick = {
                            selectedOption = "TopExpense"
                            expanded = false
                        }
                    )


                }
            }
        }
    }) {
        // Initialize viewmodel and get data from viewmodel
        val context = LocalContext.current
        val viewModel: StashVM = hiltViewModel()
        val dataState = viewModel.entries.collectAsState(emptyList())
        val topExpense = viewModel.topEntries.collectAsState(initial = emptyList())
        val topIncome = viewModel.topIncome.collectAsState(initial = emptyList())
        val incomeChartData = viewModel.incomeChartEntries.collectAsState(initial = emptyList())

        Column(modifier = Modifier.padding(it)) {
            // Get chart entries based on selected option
            val chartEntries = when (selectedOption) {
                "TopIncome" -> viewModel.getTopEntriesForIncome(incomeChartData.value)
                else -> viewModel.getEntriesForChart(dataState.value)
            }

            // Pass the dynamic chart entries and selected option
            LineChart(
                entries = chartEntries, 
                selectedOption = selectedOption
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Show data according to the selected option
            val dataToShow = when(selectedOption){
                "TopIncome" -> topIncome.value
                else -> topExpense.value
            }

            // Show different title based on selection
            val listTitle = when (selectedOption) {
                "TopIncome" -> "Top Income"
                else -> "Top Spending"
            }

            // Debug: Log data availability (remove this in production)
            if (selectedOption == "TopIncome" && topIncome.value.isEmpty()) {
                // Show a message when no income data is found
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ExpenceTextView(
                        text = "No income data found. Add some income entries first!",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                TransactionList(
                    modifier = Modifier,
                    list = dataToShow,
                    title = listTitle
                )
            }
        }
    }
}

@Composable
fun LineChart(entries: List<Entry>, selectedOption: String) {
    val context = LocalContext.current

    if (entries.isEmpty()) {
        // Show a placeholder when no data is available
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            ExpenceTextView(
                text = "No data available for chart",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        return
    }

    AndroidView(factory = {
        // this factory create layout file
        val view = LayoutInflater.from(context).inflate(R.layout.stash_line_chart, null)
        view
    }, modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)){ view->
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)

        // Set chart label and color based on selected option
        val chartLabel = if (selectedOption == "TopIncome") "Income" else "Expense"
        val chartColor = if (selectedOption == "TopIncome")
            android.graphics.Color.parseColor("#FF4CAF50") // Green for income
        else
            android.graphics.Color.parseColor("#FF2F7E79") // Original teal for expense

        val dataSet = LineDataSet(entries, chartLabel).apply {
            color = chartColor
            valueTextColor = android.graphics.Color.BLACK
            lineWidth = 3f
            axisDependency = YAxis.AxisDependency.RIGHT
            setDrawFilled(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 12f
            valueTextColor = chartColor

            // Use different gradient for income vs expense
            val drawable = if (selectedOption == "TopIncome") {
                ContextCompat.getDrawable(
                    context,
                    R.drawable.income_chart_gradient
                ) // Green gradient for income
            } else {
                ContextCompat.getDrawable(context, R.drawable.chart_gradient)
            }
            drawable?.let {
                fillDrawable = it
            }

        }

        lineChart.xAxis.valueFormatter =
            object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return Utils.formatDateForCHart(value.toLong())
                }
            }

        lineChart.data = LineData(dataSet)
        lineChart.axisLeft.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.axisRight.setDrawGridLines(false)
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.xAxis.setDrawAxisLine(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.invalidate()
    }
}