package com.example.expencetracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expencetracker.Screen.AddExpence
import com.example.expencetracker.Screen.AllTransactionsScreen
import com.example.expencetracker.Screen.BudgetManagementScreen
import com.example.expencetracker.Screen.EditProfileScreen
import com.example.expencetracker.Screen.HomeScreen
import com.example.expencetracker.Screen.LoginScreen
import com.example.expencetracker.Screen.ProfileScreen
import com.example.expencetracker.Screen.SignupScreen
import com.example.expencetracker.Screen.StashScreen
import com.example.expencetracker.ui.theme.Zinc
import com.example.expencetracker.viewmodel.AuthViewModel

@Composable
fun NavHostScreen(){
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    var bottomBarVisibility by remember {
        mutableStateOf(true)
    }

    // Check authentication status and navigate accordingly
    LaunchedEffect(Unit) {
        val startDestination = if (authViewModel.isUserLoggedIn()) "/home" else "/login"
        navController.navigate(startDestination) {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(bottomBar = {
        AnimatedVisibility(visible = bottomBarVisibility) {
            NavigationBottomBar(
                navController = navController,
                items = listOf(
                    NavItem(route = "/home", icon = R.drawable.ic_home),
                    NavItem(route = "/stash", icon = R.drawable.ic_stash),
                    NavItem(route = "/profile", icon = R.drawable.ic_profile)
                )

            )
        }
    }) {
        NavHost(
            navController = navController,
            startDestination = "/login",
            modifier = Modifier.padding(it)
        ) {
            composable(route = "/login") {
                bottomBarVisibility = false
                LoginScreen(navController)
            }

            composable(route = "/signup") {
                bottomBarVisibility = false
                SignupScreen(navController)
            }

            composable(route = "/home") {
                bottomBarVisibility = true
                HomeScreen(navController)
            }

            composable(route = "/add") {
                bottomBarVisibility = false
                AddExpence(navController)
            }

            composable(route = "/stash") {
                bottomBarVisibility = true
                StashScreen(navController)
            }

            composable(route = "/profile") {
                bottomBarVisibility = true
                ProfileScreen(navController)
            }

            composable(route = "/edit-profile") {
                bottomBarVisibility = false
                EditProfileScreen(navController)
            }

            composable(route = "/transactions") {
                bottomBarVisibility = false
                AllTransactionsScreen(navController)
            }

            composable(route = "/budget-settings") {
                bottomBarVisibility = false
                BudgetManagementScreen(navController)
            }
        }
    }


}

// crate bottom navigation item
data class NavItem(
    val icon: Int,
    val route: String
)

@Composable
fun NavigationBottomBar(
    navController: NavController,
    items: List<NavItem>

){
    // Bottom navigation bar
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    BottomAppBar {
        items.forEach {  item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(navController.graph.startDestinationId){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon), contentDescription = null,
                        modifier = Modifier.size(30.dp))
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Zinc,
                    selectedIconColor = Zinc,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray

                ))
        }
    }

}