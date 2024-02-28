package com.mastrosql.app.ui.navigation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.mastrosql.app.ui.navigation.LocalAppNavigationViewModelProvider
import com.mastrosql.app.ui.navigation.UserPreferencesViewModel
import com.mastrosql.app.ui.navigation.intro.introGraph

@Composable
fun MainCompose(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    preferencesViewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    // TODO: try to use hiltViewModel()
) {

    // Get the app navigation viewmodel from the LocalAppNavigationViewModelProvider
    val appNavigationViewModel = LocalAppNavigationViewModelProvider.current

    // Get the gestures enabled state for the drawer from the viewmodel
    val gesturesEnabled by appNavigationViewModel.gesturesEnabled

    // Get the current screen from the viewmodel
    val currentScreen by appNavigationViewModel.currentScreen

    // Get the user onboarded status from the viewmodel
    val isOnboarded by preferencesViewModel.isOnboardedUiState.collectAsState()

    //Get the user logged in status from the viewmodel
    val isLoggedIn by preferencesViewModel.isLoggedInUiState.collectAsState()

    // Get active buttons from UserPreferencesViewModel
    val activeButtonsUiState by preferencesViewModel.activeButtonsUiState.collectAsState()

    if (isLoggedIn && isOnboarded) {
        // If the user is logged in and onboarded, navigate directly to the new home screen
        LaunchedEffect(Unit) {
            navController.navigate(MainNavOption.NewHomeScreen.name) {
                popUpTo(NavRoutes.MainRoute.name) {
                    inclusive = true
                }
            }
        }
    }

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
                appNavigationViewModel
                    .setCurrentScreen(MainNavOption.valueOf(destination.route!!))
            }
        }
    }

    /*val defaultPick = when {
        isLoggedIn -> MainNavOption.NewHomeScreen
        else -> MainNavOption.LoginScreen
    }*/

    /*if (isLoggedIn) {
        appNavigationViewModel.setCurrentScreen(MainNavOption.NewHomeScreen)
    } else {
        appNavigationViewModel.setCurrentScreen(MainNavOption.LoginScreen)
    }*/

    ModalNavigationDrawer(drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            AppDrawerContent(
                drawerState = drawerState,
                menuItems = DrawerParams.createDrawerButtons(activeButtonsUiState),
                currentPick = currentScreen
                    ?: MainNavOption.NewHomeScreen,//defaultPick, //MainNavOption.LoginScreen
                onCurrentPickChange = { newCurrentPick ->
                    appNavigationViewModel.setCurrentScreen(newCurrentPick)
                },
            ) { onUserPickedOption ->

                /*
                * When the user picks an option from the drawer, navigate to the corresponding screen,
                * close the drawer and update the current screen in the viewmodel
                 */
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
                        preferencesViewModel.logout(navController)
                        //appNavigationViewModel.setCurrentScreen(MainNavOption.LoginScreen)
                    }
                }
            }
        })
    {
        NavHost(
            navController,
            startDestination =
            if (isOnboarded) {
                NavRoutes.MainRoute.name
            } else NavRoutes.IntroRoute.name
        ) {
            introGraph(navController)
            mainGraph(drawerState, navController)
        }
    }
}

enum class NavRoutes {
    IntroRoute, MainRoute
}

object DrawerParams {

    // Function to create drawer buttons based on activeButtonsUiState
    fun createDrawerButtons(activeButtonsUiState: Map<MainNavOption, Boolean>): List<AppDrawerItemInfo<MainNavOption>> {
        val buttons = arrayListOf<AppDrawerItemInfo<MainNavOption>>()

        //NewHome button always visible
        buttons.add(
            AppDrawerItemInfo(
                drawerOption = MainNavOption.NewHomeScreen,
                title = R.string.drawer_home_menu,
                icon = Icons.Default.Home,
                descriptionId = R.string.drawer_home_description
            )
        )
        if (activeButtonsUiState[MainNavOption.HomeScreen] == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.HomeScreen,
                    R.string.drawer_home,
                    Icons.Default.Home,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState[MainNavOption.CustomersScreen] == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.CustomersScreen,
                    R.string.drawer_customers,
                    Icons.Default.Person,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState[MainNavOption.CustomersPagedScreen] == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.CustomersPagedScreen,
                    R.string.drawer_customers2,
                    Icons.Default.Person,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState[MainNavOption.ArticlesScreen] == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.ArticlesScreen,
                    R.string.articles,
                    Icons.AutoMirrored.Filled.ListAlt,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState[MainNavOption.ItemsScreen] == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.ItemsScreen,
                    R.string.drawer_inventory,
                    Icons.Default.Folder,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState[MainNavOption.OrdersScreen] == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.OrdersScreen,
                    R.string.drawer_orders,
                    Icons.Default.Description,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState[MainNavOption.CartScreen] == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.CartScreen,
                    R.string.drawer_cart,
                    Icons.Default.ShoppingCart,
                    R.string.drawer_cart_description
                )
            )
        }

        //Logout button always visible
        buttons.add(
            AppDrawerItemInfo(
                MainNavOption.Logout,
                R.string.logout,
                Icons.AutoMirrored.Filled.Logout,
                R.string.drawer_logout_description
            )
        )
        return buttons
    }
}

@Preview(apiLevel = 33)
@Composable
fun MainActivityPreview() {
    MainCompose()
}