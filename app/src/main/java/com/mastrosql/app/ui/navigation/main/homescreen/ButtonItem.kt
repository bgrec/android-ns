package com.mastrosql.app.ui.navigation.main.homescreen

import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.AppNavigationViewModel
import com.mastrosql.app.ui.navigation.main.MainNavOption

data class ButtonItem(
    val labelResId: Int,
    val destination: MainNavOption,
    val action: (NavController, AppNavigationViewModel) -> Unit
)

object ButtonItemsList {
    val buttonItems = listOf(ButtonItem(labelResId = R.string.drawer_customers,
        destination = MainNavOption.CustomersScreen,
        action = { navController, appNavigationViewModel ->
            navController.navigate(MainNavOption.CustomersScreen.name) {
                popUpTo(MainNavOption.HomeScreen.name)
                appNavigationViewModel.setCurrentScreen(MainNavOption.CustomersScreen)
            }
        }), ButtonItem(labelResId = R.string.drawer_customers2,
        destination = MainNavOption.CustomersPagedScreen,
        action = { navController, appNavigationViewModel ->
            navController.navigate(MainNavOption.CustomersPagedScreen.name) {
                popUpTo(MainNavOption.HomeScreen.name)
                appNavigationViewModel.setCurrentScreen(MainNavOption.CustomersPagedScreen)
            }
        }), ButtonItem(labelResId = R.string.drawer_articles,
        destination = MainNavOption.ArticlesScreen,
        action = { navController, appNavigationViewModel ->
            navController.navigate(MainNavOption.ArticlesScreen.name) {
                popUpTo(MainNavOption.HomeScreen.name)
                appNavigationViewModel.setCurrentScreen(MainNavOption.ArticlesScreen)
            }
        }), ButtonItem(labelResId = R.string.drawer_inventory,
        destination = MainNavOption.ItemsScreen,
        action = { navController, appNavigationViewModel ->
            navController.navigate(MainNavOption.ItemsScreen.name) {
                popUpTo(MainNavOption.HomeScreen.name)
                appNavigationViewModel.setCurrentScreen(MainNavOption.ItemsScreen)
            }
        }), ButtonItem(labelResId = R.string.drawer_warehouse_out_operations,
        destination = MainNavOption.WarehouseOutOperationsScreen,
        action = { navController, appNavigationViewModel ->
            navController.navigate(MainNavOption.WarehouseOutOperationsScreen.name) {
                popUpTo(MainNavOption.HomeScreen.name)
                appNavigationViewModel.setCurrentScreen(MainNavOption.WarehouseOutOperationsScreen)
            }
        }

    ), ButtonItem(labelResId = R.string.drawer_warehouse_in_operations,
        destination = MainNavOption.WarehouseInOperationsScreen,
        action = { navController, appNavigationViewModel ->
            navController.navigate(MainNavOption.WarehouseInOperationsScreen.name) {
                popUpTo(MainNavOption.HomeScreen.name)
                appNavigationViewModel.setCurrentScreen(MainNavOption.WarehouseInOperationsScreen)
            }
        }

    ), ButtonItem(labelResId = R.string.drawer_orders,
        destination = MainNavOption.OrdersScreen,
        action = { navController, appNavigationViewModel ->
            navController.navigate(MainNavOption.OrdersScreen.name) {
                popUpTo(MainNavOption.HomeScreen.name)
                appNavigationViewModel.setCurrentScreen(MainNavOption.OrdersScreen)
            }
        }))
}
