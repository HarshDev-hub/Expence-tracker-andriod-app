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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.expencetracker.R
import com.example.expencetracker.Utils
import com.example.expencetracker.viewmodel.HomeVM
import com.example.expencetracker.viewmodel.StashVM
import com.example.expencetracker.viewmodel.StashVMFactory
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

    Scaffold(topBar = {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp, start =16.dp, end = 16.dp, bottom = 8.dp )) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.align(Alignment.CenterStart)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            ExpenceTextView(
                text = "Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier =  Modifier.padding(16.dp)
                    .align(Alignment.Center)
            )
            // back button
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
                                "TopIncome",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.End
                            )
                        },
                        onClick = {

                        }
                    )

                    DropdownMenuItem(
                        text = {
                            ExpenceTextView(
                                "TopExpense",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.End
                            )
                        },
                        onClick = {

                        }
                    )


                }
            }
        }
    }) {
        // inital viewmodel and get data from viewmodel
        val viewModel = StashVMFactory(navController.context).create(StashVM::class.java)
        val dataState = viewModel.entries.collectAsState(emptyList())
        val topExpense = viewModel.topEntries.collectAsState(initial = emptyList())
        val topIncome = viewModel.topIncome.collectAsState(initial = emptyList())
        Column(modifier = Modifier.padding(it)) {
            val entris = viewModel.getEntriesForChart(dataState.value)
        LineChart(entries = entris)
            Spacer(modifier = Modifier.height(16.dp))
            TransactionList(
                Modifier, list = topExpense.value, "Top Spending"
            )
        }
    }
}

@Composable
fun LineChart(entries: List<Entry>){

    val context = LocalContext.current
    AndroidView(factory = {
        // this factory create layout file
        val view = LayoutInflater.from(context).inflate(R.layout.stash_line_chart, null)
        view
    }, modifier = Modifier.fillMaxWidth().height(250.dp)){ view->
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)
        val dataSet = LineDataSet(entries,"Expense").apply {
            color = android.graphics.Color.parseColor("#FF2F7E79")
            valueTextColor = android.graphics.Color.BLACK
            lineWidth = 3f
            axisDependency = YAxis.AxisDependency.RIGHT
            setDrawFilled(true)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 12f
            valueTextColor = android.graphics.Color.parseColor("#FF2F7E79")
            // link the gradinet drawble into the chart
            val drawable = ContextCompat.getDrawable(context, R.drawable.chart_gradient)
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