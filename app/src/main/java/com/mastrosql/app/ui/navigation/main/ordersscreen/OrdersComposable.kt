package com.mastrosql.app.ui.navigation.main.ordersscreen

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * Top level composable that represents screens for the orders.
 */

@Composable
fun OrdersComposable(
    drawerState: DrawerState,
    navController: NavHostController = rememberNavController()
) {
     /*
        * This is the main entry point for the Orders screen.
     */
    OrdersNavHost(drawerState = drawerState, navController = navController)
}

