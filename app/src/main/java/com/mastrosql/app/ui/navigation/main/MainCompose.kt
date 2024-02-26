package com.mastrosql.app.ui.navigation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.appdrawer.AppDrawerContent
import com.mastrosql.app.ui.components.appdrawer.AppDrawerItemInfo
import com.mastrosql.app.ui.navigation.intro.IntroViewModel
import com.mastrosql.app.ui.navigation.intro.introGraph

@Composable
fun MainCompose(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    preferencesViewModel: IntroViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appNavigationViewModel: AppNavigationViewModel = viewModel(factory = AppViewModelProvider.Factory)
    // TODO: try to use hiltViewModel()
) {
    // verify if using appNavigationViewModel by viewModel(factory = AppViewModelProvider.Factory) is correct
    // or if it should be appNavigationViewModel: AppNavigationViewModel = AppViewModelProvider.LocalAppNavigationViewModelProvider.current

    // it was like this
    //val appNavigationViewModel: AppNavigationViewModel = AppViewModelProvider.LocalAppNavigationViewModelProvider.current
    //val app2NavigationViewModel: AppNavigationViewModel = viewModel(factory =  AppViewModelProvider.Factory)

    // Get the gestures enabled state for the drawer from the viewmodel
    val gesturesEnabled by appNavigationViewModel.gesturesEnabled

    // Get the current screen from the viewmodel
    val currentScreen by appNavigationViewModel.currentScreen

    // Update current screen when navigating
    navController.addOnDestinationChangedListener { _, destination, _ ->
        when (destination.route) {
            MainNavOption.LoginScreen.name,
            MainNavOption.NewHomeScreen.name,
            MainNavOption.HomeScreen.name,
            MainNavOption.CustomersScreen.name,
            MainNavOption.CustomersPagedScreen.name,
            MainNavOption.ArticlesScreen.name,
            MainNavOption.ItemsScreen.name,
            MainNavOption.OrdersScreen.name,
            MainNavOption.SettingsScreen.name,
            MainNavOption.CartScreen.name,
            MainNavOption.Logout.name -> {
                appNavigationViewModel.setCurrentScreen(MainNavOption.valueOf(destination.route!!))
            }
        }
    }


    //TO-DO get the user logged in status from the viewmodel
    val isUserLoggedIn = false //viewModel.isUserLoggedIn.collectAsState()
    val defaultPick = when {
        isUserLoggedIn -> MainNavOption.NewHomeScreen // For example, if the user is logged in, default to the home screen
        else -> MainNavOption.LoginScreen // Otherwise, default to the login screen
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            AppDrawerContent(
                drawerState = drawerState,
                menuItems = DrawerParams.drawerButtons,
                currentPick = currentScreen ?: defaultPick, //MainNavOption.LoginScreen
                onCurrentPickChange = { newCurrentPick ->
                    appNavigationViewModel.setCurrentScreen(newCurrentPick)
                },
            ) { onUserPickedOption ->
                appNavigationViewModel.setCurrentScreen(onUserPickedOption)
                when (onUserPickedOption) {
                    MainNavOption.LoginScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name) {
                                inclusive = true
                            }
                        }
                    }

                    MainNavOption.NewHomeScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name) {
                                inclusive = true
                            }
                        }
                    }

                    MainNavOption.HomeScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.NewHomeScreen.name)
                        }
                    }

                    MainNavOption.CustomersScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.NewHomeScreen.name)
                        }
                    }

                    MainNavOption.CustomersPagedScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.NewHomeScreen.name)
                        }
                    }

                    MainNavOption.ArticlesScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.NewHomeScreen.name)
                        }
                    }

                    MainNavOption.ItemsScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.NewHomeScreen.name)
                        }
                    }

                    MainNavOption.OrdersScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.NewHomeScreen.name)
                        }
                    }

                    MainNavOption.SettingsScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.NewHomeScreen.name)
                        }
                    }

                    MainNavOption.CartScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.NewHomeScreen.name)
                        }
                    }

                    MainNavOption.Logout -> {
                        //TO-DO Handle logout
                        preferencesViewModel.logoutUser()
                    }
                }
            }
        }) {
        // call the navigation graph
        val isOnboarded = preferencesViewModel.isOnboarded.collectAsState()

        // Disable gestures on drawer if the user is not onboarded
        /*if (!isOnboarded.value) {
            appNavigationViewModel.setGesturesEnabled(false)
        }*/

        NavHost(
            navController,
            startDestination = if (isOnboarded.value) NavRoutes.MainRoute.name else NavRoutes.IntroRoute.name
        ) {
            introGraph(navController)
            mainGraph(drawerState, navController)
        }
    }
}

enum class NavRoutes {
    IntroRoute,
    MainRoute
}

object DrawerParams {
    val drawerButtons = arrayListOf(
        AppDrawerItemInfo(
            MainNavOption.LoginScreen,
            R.string.drawer_login,
            Icons.AutoMirrored.Filled.Login,
            R.string.drawer_login_description
        ), AppDrawerItemInfo(
            MainNavOption.NewHomeScreen,
            R.string.drawer_new_home,
            Icons.Default.Home,
            R.string.drawer_home_description
        ), AppDrawerItemInfo(
            MainNavOption.HomeScreen,
            R.string.drawer_home,
            Icons.Default.Home,
            R.string.drawer_home_description
        ), AppDrawerItemInfo(
            MainNavOption.CustomersScreen,
            R.string.drawer_customers,
            Icons.Default.Person,
            R.string.drawer_home_description
        ), AppDrawerItemInfo(
            MainNavOption.CustomersPagedScreen,
            R.string.drawer_customers2,
            Icons.Default.Person,
            R.string.drawer_home_description
        ), AppDrawerItemInfo(
            MainNavOption.ArticlesScreen,
            R.string.articles,
            Icons.AutoMirrored.Filled.ListAlt,
            R.string.drawer_home_description
        ), AppDrawerItemInfo(
            MainNavOption.ItemsScreen,
            R.string.drawer_inventory,
            Icons.Default.Folder,
            R.string.drawer_home_description
        ), AppDrawerItemInfo(
            MainNavOption.OrdersScreen,
            R.string.drawer_orders,
            Icons.Default.Description,
            R.string.drawer_home_description
        ), AppDrawerItemInfo(
            MainNavOption.SettingsScreen,
            R.string.drawer_settings,
            Icons.Default.Settings,
            R.string.drawer_settings_description
        ),
        AppDrawerItemInfo(
            MainNavOption.CartScreen,
            R.string.drawer_cart,
            Icons.Default.ShoppingCart,
            R.string.drawer_cart_description
        ), AppDrawerItemInfo(
            MainNavOption.Logout,
            R.string.logout,
            Icons.AutoMirrored.Filled.Logout,
            R.string.drawer_logout_description
        )
    )
}

@Preview(apiLevel = 33)
@Composable
fun MainActivityPreview() {
    MainCompose()
}