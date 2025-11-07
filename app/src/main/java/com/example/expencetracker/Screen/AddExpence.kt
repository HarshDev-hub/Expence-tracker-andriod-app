package com.example.expencetracker.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun AddExpence(navController: NavController){
    val viewmodel: AddExpenseVM = hiltViewModel()

    // create coroutine
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow,list,card,topBar)= createRefs()
            Image(painter = painterResource(R.drawable.ic_topbar),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(nameRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            navController.popBackStack()
                        }
                )
                Text(
                    text = "Add Expense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier =  Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
                // back button
                Image(
                    painter = painterResource(R.drawable.ic_dot),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(21.dp)
                )
            }

            DataForm(modifier = Modifier
                .padding(top = 60.dp)
                .constrainAs(card) {
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }, onAddExpenseClick = {
                coroutineScope.launch {
                    if(viewmodel.addExpense(it)){
                        navController.popBackStack()
                    }
                }
            })
        }
    }
}

@Composable
fun DataForm(modifier: Modifier, onAddExpenseClick:(model:ExpenseEntity)->Unit) {

    val name = remember {
        mutableStateOf("") // its use for take the input rom user
    }
    val amount = remember {
        mutableStateOf("") // its use for take the input rom user
    }
    val date = remember { // for date remebering
        mutableStateOf(0L)
    }
    val dateDialogVisibility = remember {
        mutableStateOf(false)
    }
    val category = remember {
        mutableStateOf("")
    }
    val type = remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())

    ) {

        ExpenceTextView(text = "Name", fontSize = 14.sp)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = name.value,
            onValueChange = {
                name.value = it
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))
        ExpenceTextView(text = "Amount", fontSize = 14.sp)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = amount.value,
            onValueChange = {
                amount.value = it
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(8.dp))
        // date
        ExpenceTextView(text = "Date", fontSize = 14.sp)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = if(date.value == 0L) "" else Utils.formatDateToHumanReadableForm(date.value),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { dateDialogVisibility.value = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.size(8.dp))
        // dropdown
        ExpenceTextView(text = "Category", fontSize = 14.sp)
        Spacer(modifier = Modifier.size(4.dp))
            ExpenseDropDown(listOf("Netflix", "Paypal", "Starbuck", "Salary", "Upwork", "Gpay"),
                onItemSelected = {
                    category.value = it
                })
        Spacer(modifier = Modifier.size(8.dp))
        //type
        ExpenceTextView(text = "Type", fontSize = 14.sp)
        Spacer(modifier = Modifier.size(4.dp))
        ExpenseDropDown(listOf("Income", "Expense"),
            onItemSelected = {
                type.value = it
            })
        Spacer(modifier = Modifier.size(8.dp))

        Button(
            onClick = {
                val model = ExpenseEntity(
                   null,
                    name.value,
                    amount = amount.value.toDoubleOrNull()?:0.0,
                    Utils.formatDateToHumanReadableForm(date.value),
                    category = category.value,
                    type = type.value
                )
                onAddExpenseClick(model)
            },
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
        ) {
            ExpenceTextView(
                text = "Add Expense",
                fontSize = 14.sp,
                color = Color.White
            )
        }

    }
    if (dateDialogVisibility.value) {
        ExpenseDatePickerDialog(onDateSelected = {date.value = it
            dateDialogVisibility.value = false}, onDismiss = {
                dateDialogVisibility.value = false
            })
    }
}

@Composable
@Preview(showBackground = true)
fun AddExpencePreview(){
    AddExpence(rememberNavController())

}