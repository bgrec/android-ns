package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

/**
 * Provides Navigation graph for the items application.
 */
@Composable
fun OrderDetailsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = OrderDetailsDestination.route,
        modifier = modifier
    ) {
        composable(route = OrderDetailsDestination.route) {
            OrderDetailsScreen(
                navigateToItemEntry = { navController.navigate(OrderDetailsEntryDestination.route) },
               // navigateToItemUpdate = {
                navigateBack = {
                    navController.navigate("${OrderDetailsDestination.route}/${it}")
                },
                navController = navController,
                drawerState = DrawerState(DrawerValue.Closed) //TODO add drawer state here
            )
        }
        composable(route = OrderDetailsEntryDestination.route) {
            OrderDetailsEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = OrderDetailsItemEditDestination.routeWithArgs,
            arguments = listOf(navArgument(OrderDetailsItemEditDestination.rowIdArg) {
                type = NavType.IntType
            })
        ) {
            OrderDetailsScreen(
                //navigate = { navController.navigate("${OrderDetailsItemEditDestination.route}/$it") },
                navigateToItemEntry = { navController.navigate(OrderDetailsEntryDestination.route) },
                navigateBack = { navController.navigateUp() },
                navController = navController,
                drawerState = DrawerState(DrawerValue.Closed), //TODO add drawer state here

            )
        }
        composable(
            route = OrderDetailsItemEditDestination.routeWithArgs,
            arguments = listOf(navArgument(OrderDetailsItemEditDestination.rowIdArg) {
                type = NavType.IntType
            })
        ) {
            OrderDetailsItemEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
