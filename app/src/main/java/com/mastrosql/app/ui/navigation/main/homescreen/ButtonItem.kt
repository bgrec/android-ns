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
    val buttonItems = listOf(
        ButtonItem(
            labelResId = R.string.drawer_customers,
            destination = MainNavOption.CustomersScreen,
            action = { navController, appNavigationViewModel ->
                navController.navigate(MainNavOption.CustomersScreen.name) {
                    popUpTo(MainNavOption.HomeScreen.name)
                    appNavigationViewModel.setCurrentScreen(MainNavOption.CustomersScreen)
                }
            }
        ),
        ButtonItem(
            labelResId = R.string.drawer_customers2,
            destination = MainNavOption.CustomersPagedScreen,
            action = { navController, appNavigationViewModel ->
                navController.navigate(MainNavOption.CustomersPagedScreen.name) {
                    popUpTo(MainNavOption.HomeScreen.name)
                    appNavigationViewModel.setCurrentScreen(MainNavOption.CustomersPagedScreen)
                }
            }
        ),
        ButtonItem(
            labelResId = R.string.drawer_articles,
            destination = MainNavOption.ArticlesScreen,
            action = { navController, appNavigationViewModel ->
                navController.navigate(MainNavOption.ArticlesScreen.name) {
                    popUpTo(MainNavOption.HomeScreen.name)
                    appNavigationViewModel.setCurrentScreen(MainNavOption.ArticlesScreen)
                }
            }
        ),
        ButtonItem(
            labelResId = R.string.drawer_inventory,
            destination = MainNavOption.ItemsScreen,
            action = { navController, appNavigationViewModel ->
                navController.navigate(MainNavOption.ItemsScreen.name) {
                    popUpTo(MainNavOption.HomeScreen.name)
                    appNavigationViewModel.setCurrentScreen(MainNavOption.ItemsScreen)
                }
            }
        ),
        ButtonItem(
            labelResId = R.string.drawer_orders,
            destination = MainNavOption.OrdersScreen,
            action = { navController, appNavigationViewModel ->
                navController.navigate(MainNavOption.OrdersScreen.name) {
                    popUpTo(MainNavOption.HomeScreen.name)
                    appNavigationViewModel.setCurrentScreen(MainNavOption.OrdersScreen)
                }
            }
        )

    )

}
/*
* if (activeButtonsUiState[MainNavOption.CustomersScreen] == true) {
            Spacer(Modifier.height(30.dp))
            AppButton(
                modifier = buttonsModifier,
                text = R.string.drawer_customers,
                onClick = {
                    navController.navigate(MainNavOption.CustomersScreen.name) {
                        popUpTo(MainNavOption.NewHomeScreen.name)
                        appNavigationViewModel.setCurrentScreen(MainNavOption.CustomersScreen)
                    }
                })
        }

        if (activeButtonsUiState[MainNavOption.CustomersPagedScreen] == true) {
            Spacer(modifier = Modifier.height(10.dp))
            AppButton(
                modifier = buttonsModifier,
                text = R.string.drawer_customers2,
                onClick = {
                    navController.navigate(MainNavOption.CustomersPagedScreen.name) {
                        popUpTo(MainNavOption.NewHomeScreen.name)
                        appNavigationViewModel.setCurrentScreen(MainNavOption.CustomersPagedScreen)
                    }
                })
        }

        if (activeButtonsUiState[MainNavOption.ArticlesScreen] == true) {
            Spacer(modifier = Modifier.height(10.dp))

            AppButton(
                modifier = buttonsModifier,
                text = R.string.drawer_articles,
                onClick = {
                    navController.navigate(MainNavOption.ArticlesScreen.name) {
                        popUpTo(MainNavOption.NewHomeScreen.name)
                        appNavigationViewModel.setCurrentScreen(MainNavOption.ArticlesScreen)
                    }
                })
        }

        if (activeButtonsUiState[MainNavOption.ItemsScreen] == true) {
            Spacer(modifier = Modifier.height(10.dp))

            AppButton(
                modifier = buttonsModifier,
                text = R.string.drawer_inventory,
                onClick = {
                    navController.navigate(MainNavOption.ItemsScreen.name) {
                        popUpTo(MainNavOption.NewHomeScreen.name)
                        appNavigationViewModel.setCurrentScreen(MainNavOption.ItemsScreen)
                    }
                })
        }

        if (activeButtonsUiState[MainNavOption.OrdersScreen] == true) {
            Spacer(modifier = Modifier.height(10.dp))
            AppButton(modifier = buttonsModifier, text = R.string.drawer_orders, onClick = {
                navController.navigate(MainNavOption.OrdersScreen.name) {
                    popUpTo(MainNavOption.NewHomeScreen.name)
                    appNavigationViewModel.setCurrentScreen(MainNavOption.OrdersScreen)
                }
            })
        }
* */