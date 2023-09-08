package com.mastrosql.app.ui.navigation.main

import androidx.compose.material3.DrawerState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.intro.composables.AboutScreen
import com.mastrosql.app.ui.navigation.main.cartScreen.CartScreen
import com.mastrosql.app.ui.navigation.main.cartScreen.CartViewModel
import com.mastrosql.app.ui.navigation.main.customersScreen.CustomersMasterDataViewModel
import com.mastrosql.app.ui.navigation.main.customersScreen.CustomersScreen
import com.mastrosql.app.ui.navigation.main.homescreen.HomeScreen
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemsComposable
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemsScreen
import com.mastrosql.app.ui.navigation.main.loginscreen.LoginScreen
import com.mastrosql.app.ui.navigation.main.settingsscreen.SettingsScreen

fun NavGraphBuilder.mainGraph(drawerState: DrawerState, navController: NavController) {
    //val cartItems = listOf<Item>()
    navigation(
        startDestination = MainNavOption.LoginScreen.name,
        route = NavRoutes.MainRoute.name
    ) {
        composable(MainNavOption.LoginScreen.name) {
            LoginScreen(drawerState, navController)
        }
        composable(MainNavOption.HomeScreen.name) {
            HomeScreen(drawerState, navController, viewModel = CartViewModel())
        }
        composable(MainNavOption.CustomersScreen.name) {
            
            /* ok funziona
            val customersMasterDataViewModel: CustomersMasterDataViewModel =
        
                viewModel(factory = AppViewModelProvider.Factory)
            CustomersScreen(
                customersUiState = customersMasterDataViewModel.customersUiState,
                retryAction = customersMasterDataViewModel::getCustomersMasterData,
                drawerState,
                navController
            )
            */
            ItemsComposable()
            
            //HomeScreen(drawerState, navController, viewModel = CartViewModel())
        }
        composable(MainNavOption.SettingsScreen.name) {
            SettingsScreen(drawerState)
        }
        composable(MainNavOption.AboutScreen.name) {
            AboutScreen(drawerState)
        }
        composable(MainNavOption.CartScreen.name) {
            CartScreen(drawerState, navController)
        }
    }
}

enum class MainNavOption {
    LoginScreen,
    HomeScreen,
    CustomersScreen,
    AboutScreen,
    SettingsScreen,
    CartScreen
}
