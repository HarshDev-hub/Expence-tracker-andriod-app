package com.example.expencetracker.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.expencetracker.R
import com.example.expencetracker.Utils
import com.example.expencetracker.data.model.ExpenseEntity
import com.example.expencetracker.viewmodel.HomeVM
import com.example.expencetracker.viewmodel.HomeVMFactory
import com.example.expencetracker.ui.theme.Zinc
import com.example.expencetracker.viewmodel.StashVM
import com.example.expencetracker.widget.ExpenceTextView

@Composable
fun HomeScreen(navController: NavController){
    val viewModel: HomeVM = HomeVMFactory(LocalContext.current).create(HomeVM::class.java)
    val coroutineScope = rememberCoroutineScope()
    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow,list,card,topBar, add)= createRefs()
            Image(painter = painterResource(R.drawable.ic_topbar),contentDescription = null,
                modifier = Modifier.constrainAs(topBar){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(nameRow){
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }){

                Column {
                    ExpenceTextView(
                        text = "Good Morning",
                        fontSize = 17.sp,
                        color = Color.White)
                    ExpenceTextView(
                        text = "CodeWithHk",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Image(
                    painter = painterResource(R.drawable.ic_notification),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .size(40.dp)
                )
            }

            val state = viewModel.expenses.collectAsState(initial = emptyList())
            val expenses = viewModel.getTotalExpense(state.value)
            val income = viewModel.getTotalIncome(state.value)
            val balance = viewModel.getBalance(state.value)

            cardItem(
                modifier = Modifier.constrainAs(card){
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                balance = balance,
                income = income,
                expenses = expenses
            )

            TransactionList(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(list){
                        top.linkTo(card.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
                list = state.value,
                onDeleteClick = { item -> viewModel.deleteExpense(item) }
            )

            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_add),
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(add){
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate("/add")
                    }
            )
        }
    }
}

@Composable
fun TransactionList(
    modifier: Modifier,
    list: List<ExpenseEntity>,
    title: String = "Recent Transactions",
    onDeleteClick: (ExpenseEntity) -> Unit = {}
) {
    LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                ExpenceTextView(text = title, fontSize = 17.sp)
                if (title == "Recent Transactions") {
                    ExpenceTextView(
                        text = "seeAll",
                        fontSize = 17.sp,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
        items(list) { item ->
            val icon = Utils.getItemIcon(item)
            val state = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onDeleteClick(item)
                        true
                    } else false
                }
            )

            SwipeToDismissBox(
                state = state,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp), // spacing for delete icon
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
            ) {
                TransactionItem(
                    title = item.title,
                    amount = item.amount.toString(),
                    icon = icon!!,
                    date = item.date,
                    color = if (item.type == "Income") Color.Green else Color.Red
                )
            }
        }
    }
}

@Composable
fun cardItem(modifier: Modifier,balance:String,income:String,expenses:String){

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Zinc)
            .padding(16.dp)

    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)){
           Column(modifier = Modifier.align(Alignment.CenterStart)) {
               ExpenceTextView(text = "Total Balance",fontSize = 17.sp,color = Color.White)
               ExpenceTextView(
                   text = balance,
                   fontSize = 20.sp,
                   fontWeight = FontWeight.Bold,
                   color = Color.White
               )
           }
            Image(
                painter = painterResource(R.drawable.ic_dot),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
                    .size(21.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth().
                weight(1f),
        ) {

          cardRowItem(
              modifier = Modifier.align(Alignment.CenterStart),
              title = "Income",
              amount = income,
              image = R.drawable.ic_income
          )
            cardRowItem(
                modifier = Modifier.align(Alignment.CenterEnd),
                title = "Expense",
                amount = expenses,
                image = R.drawable.ic_expence
            )

        }
    }

}

@Composable
fun cardRowItem(
    modifier: Modifier,
    title:String,
    amount:String,
    image:Int
){

    Column(modifier = modifier) {
        Row {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.size(8.dp))
            ExpenceTextView(text = title,fontSize = 17.sp,color = Color.White)
        }
        ExpenceTextView(text = amount,fontSize = 17.sp,color = Color.White)
    }

}

@Composable
fun TransactionItem(
    title:String,
    amount:String,
    icon:Int,
    date:String,
    color: Color
){
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)){
        Row {
            Image(painter = painterResource(icon), contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                ExpenceTextView(text = title,fontSize = 17.sp, fontWeight = FontWeight.Medium)
                ExpenceTextView(text = date,fontSize = 13.sp)
            }
        }
        ExpenceTextView(
            text = amount,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterEnd),
            color = color,
            fontWeight = FontWeight.SemiBold
        )

    }
}
@Composable
@Preview(showBackground = true)
fun previewHomeScreen(){
    HomeScreen(rememberNavController())
}