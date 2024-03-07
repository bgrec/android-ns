package com.mastrosql.app.ui.navigation.main

import androidx.compose.material3.DrawerState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
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
import com.mastrosql.app.ui.navigation.main.ordersscreen.OrdersComposable
import com.mastrosql.app.ui.navigation.main.settingsscreen.SettingsScreen

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
        composable(MainNavOption.OrdersComposable.name) {
            //OrdersScreen(drawerState = drawerState, navController = navController)
            OrdersComposable(drawerState = drawerState)
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
    OrdersComposable,
    AboutScreen,
    Logout
}
