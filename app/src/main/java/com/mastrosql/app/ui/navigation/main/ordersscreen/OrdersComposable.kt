package com.mastrosql.app.ui.navigation.main.ordersscreen

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * Composable that displays the Orders screen.

 */
@Composable
fun OrdersComposable(
    drawerState: DrawerState, navController: NavHostController = rememberNavController()
) {
    OrdersNavHost(drawerState = drawerState, navController = navController)
}

