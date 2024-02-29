package com.mastrosql.app.ui.navigation.main.ordersscreen

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetailsDestination
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetailsScreen


/**
 * Provides Navigation graph for Orders navigation.
 */


@Composable
fun OrdersNavHost(
    drawerState: DrawerState,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = OrdersResultDestination.route,
        //startDestination = OrderDetailsDestination.route,
        modifier = modifier
    ) {
        composable(route = OrdersResultDestination.route) {
            OrdersScreen(
                navigateToOrderEntry ={},// { navController.navigate(OrderEntryDestination.route) },
                navigateToOrderDetails = { orderId, orderDescription ->
                    navController.navigate("${OrderDetailsDestination.route}/${orderId}?orderDescription=${orderDescription}") {
                        //TODO verify if launchSigleTop is  needed
                        launchSingleTop = true
                    }
                },
                navController = navController,
                drawerState = drawerState
            )
        }

        composable(
            route = OrderDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(OrderDetailsDestination.orderIdArg) {
                type = NavType.IntType
            }, )
        ) {
                OrderDetailsScreen(
                    navigateToEditItem = {},//{ navController.navigate("${ItemEditDestination.route}/$id") },
                    navigateBack = { navController.navigateUp() },
                    navController = navController,
                    drawerState = drawerState
                )

        }/*composable(route = OrderEntryDestination.route) {
            OrderEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = OrderDetailDestination.routeWithArgs,
            arguments = listOf(navArgument(OrderDetailDestination.rowIdArg) {
                type = NavType.IntType
            })
        ) {
            OrderDetailScreen(
                navigateToEditItem = { navController.navigate("${OrderEditDestination.route}/$it") },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = OrderEditDestination.routeWithArgs,
            arguments = listOf(navArgument(OrderEditDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            OrderEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }*/
    }
}



