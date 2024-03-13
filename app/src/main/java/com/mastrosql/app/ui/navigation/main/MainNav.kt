package com.mastrosql.app.ui.navigation.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.mastrosql.app.ui.navigation.main.aboutscreen.AboutScreen
import com.mastrosql.app.ui.navigation.main.articlesscreen.ArticlesScreen
import com.mastrosql.app.ui.navigation.main.cartscreen.CartScreen
import com.mastrosql.app.ui.navigation.main.cartscreen.CartViewModel
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersPagedScreen
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersScreen
import com.mastrosql.app.ui.navigation.main.homescreen.HomeScreen
import com.mastrosql.app.ui.navigation.main.homescreen.NewHomeScreen
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemsComposable
import com.mastrosql.app.ui.navigation.main.loginscreen.LoginScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.OrdersResultDestination
import com.mastrosql.app.ui.navigation.main.ordersscreen.OrdersScreen
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetailsDestination
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetailsScreen
import com.mastrosql.app.ui.navigation.main.settingsscreen.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(drawerState: DrawerState, navController: NavController) {

    navigation(
        startDestination = MainNavOption.LoginScreen.name,
        route = NavRoutes.MainRoute.name
    ) {
        composable(MainNavOption.LoginScreen.name) {
            LoginScreen(navController)
        }
        composable(MainNavOption.NewHomeScreen.name) {
            NewHomeScreen(drawerState, navController)
        }
        composable(MainNavOption.HomeScreen.name) {
            HomeScreen(drawerState, navController, viewModel = CartViewModel())
        }
        composable(MainNavOption.CustomersScreen.name) {
            CustomersScreen(drawerState = drawerState, navController = navController)
        }
        composable(MainNavOption.CustomersPagedScreen.name) {
            CustomersPagedScreen(drawerState = drawerState, navController = navController)
        }
        composable(MainNavOption.ArticlesScreen.name) {
            ArticlesScreen(drawerState = drawerState, navController = navController)
        }

        /*
        *  Orders Screen navigation graph with nested navigation
         */
        navigation(
            startDestination = OrdersResultDestination.route,
            route = MainNavOption.OrdersScreen.name
            //route = OrdersResultDestination.route
        ) {
            composable(route = OrdersResultDestination.route) {
                OrdersScreen(
                    navigateToOrderEntry = {},// { navController.navigate(OrderEntryDestination.route) },
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
                arguments = listOf(
                    navArgument(OrderDetailsDestination.ORDER_ID_ARG) {
                        type = NavType.IntType
                    },
                )
            ) {
                OrderDetailsScreen(
                    navigateToEditItem = {},//{ navController.navigate("${ItemEditDestination.route}/$id") },
                    navigateToNewItem = { orderId ->

                        navController.navigate("${MainNavOption.ArticlesScreen.name}/?documentType=order?id=${orderId}") {
                            //TODO verify if launchSigleTop is  needed
                            launchSingleTop = true
                        }

                    },
                    navigateBack = {
                        navController.navigateUp()
                    },
                    navController = navController,
                    drawerState = drawerState
                )

            }

            // Composable for navigating to the ArticleScreen with parameters
            composable(
                route = "${MainNavOption.ArticlesScreen.name}/?documentType={documentType}?id={documentId}",
                arguments = listOf(
                    navArgument("documentType") { type = NavType.StringType },
                    navArgument("documentId") { type = NavType.IntType },
                )
            ) { backStackEntry ->
                val documentType = backStackEntry.arguments?.getString("documentType")
                val orderId = backStackEntry.arguments?.getInt("documentId") ?: -1
                // You can pass documentType and orderId to ArticlesScreen
                ArticlesScreen(
                    drawerState = drawerState,
                    navController = navController
                )
            }

            /*    NavHost(
        navController = navController,
        startDestination = OrdersResultDestination.route,
        //startDestination = OrderDetailsDestination.route,
        modifier = modifier
    ) {
        composable(route = OrdersResultDestination.route) {
            OrdersScreen(
                navigateToOrderEntry = {},// { navController.navigate(OrderEntryDestination.route) },
                navigateToOrderDetails = { orderId, orderDescription ->

                    appNavigationViewModel.setGesturesEnabled(false)
                    navController.navigate("${OrderDetailsDestination.route}/${orderId}?orderDescription=${orderDescription}") {
                        //TODO verify if launchSigleTop is  needed
                        launchSingleTop = true
                        Log.d("OrdersNavHost", "appNavigationViewModel: ${appNavigationViewModel.gesturesEnabled.value}")
                    }

                },
                navController = navController,
                drawerState = drawerState
            )
        }

        composable(
            route = OrderDetailsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(OrderDetailsDestination.orderIdArg) {
                    type = NavType.IntType
                },
            )
        ) {
            OrderDetailsScreen(
                navigateToEditItem = {},//{ navController.navigate("${ItemEditDestination.route}/$id") },
                navigateBack = {
                    navController.navigateUp()
                },
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
    }*/
        }

        composable(MainNavOption.ItemsScreen.name) {
            ItemsComposable()
        }
        composable(MainNavOption.SettingsScreen.name) {
            SettingsScreen(navController)
        }
        composable(MainNavOption.CartScreen.name) {
            CartScreen(drawerState, navController)
        }
        composable(MainNavOption.AboutScreen.name) {
            AboutScreen(navController, LocalContext.current)
        }
    }
}

enum class MainNavOption {
    LoginScreen,
    NewHomeScreen,
    HomeScreen,
    CustomersScreen,
    CustomersPagedScreen,
    ItemsScreen,
    SettingsScreen,
    CartScreen,
    ArticlesScreen,
    OrdersScreen,
    AboutScreen,
    Logout
}
