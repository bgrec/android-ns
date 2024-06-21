package com.mastrosql.app.ui.navigation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.appdrawer.AppDrawerContent
import com.mastrosql.app.ui.components.appdrawer.AppDrawerItemInfo
import com.mastrosql.app.ui.navigation.LocalAppNavigationViewModelProvider
import com.mastrosql.app.ui.navigation.intro.introGraph
import com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents.OrdersDestination
import com.mastrosql.app.ui.navigation.main.settingsscreen.UserPreferencesViewModel
import com.mastrosql.app.ui.theme.MastroAndroidPreviewTheme
import com.mastrosql.app.ui.theme.MastroAndroidTheme

/**
 * Main composable for the app
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun MainCompose(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    viewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    // TODO: try to use hiltViewModel()
) {

    // Get the app navigation viewmodel from the LocalAppNavigationViewModelProvider
    val appNavigationViewModel = LocalAppNavigationViewModelProvider.current

    // Get the gestures enabled state for the drawer from the viewmodel
    val gesturesEnabled by appNavigationViewModel.gesturesEnabled

    // Get the current screen from the viewmodel
    val currentScreen by appNavigationViewModel.currentScreen

    // Collect the state from the view model and update the UI
    val userPreferencesUiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (userPreferencesUiState.isLoggedIn && userPreferencesUiState.isOnboarded) {
        // If the user is logged in and onboarded, navigate directly to the new home screen
        LaunchedEffect(Unit) {
            navController.navigate(MainNavOption.HomeScreen.name) {
                popUpTo(NavRoutes.MainRoute.name) {
                    inclusive = true
                }
            }
        }
    }

    // Update currentScreen when navigating
    navController.addOnDestinationChangedListener { _, destination, _ ->
        when (destination.route) {
            MainNavOption.LoginScreen.name, MainNavOption.HomeScreen.name, MainNavOption.OldHomeScreen.name, MainNavOption.CustomersScreen.name, MainNavOption.CustomersPagedScreen.name, MainNavOption.ArticlesScreen.name, MainNavOption.ItemsScreen.name, MainNavOption.WarehouseOutOperationsScreen.name, MainNavOption.OrdersScreen.name, MainNavOption.SettingsScreen.name, MainNavOption.CartScreen.name, MainNavOption.AboutScreen.name, MainNavOption.Logout.name -> {
                appNavigationViewModel.setCurrentScreen(MainNavOption.valueOf(destination.route!!))
            }

            else -> {
                // If the destination is not a main screen we need to handle gestures differently
                // for each screen
                if (destination.route == OrdersDestination.route) {
                    appNavigationViewModel.setGesturesEnabled(true)
                } else {
                    appNavigationViewModel.setGesturesEnabled(false)
                }
            }
        }
    }

    // Draw the navigation drawer
    NavigationDrawerComposable(drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        navHostController = navController,
        isOnboarded = userPreferencesUiState.isOnboarded,
        activeButtons = userPreferencesUiState.activeButtons,
        currentScreen = currentScreen,
        onNewCurrentPickChange = { newCurrentPick ->
            appNavigationViewModel.setCurrentScreen(newCurrentPick)
        },
        onLogout = { navHostController ->
            viewModel.logout(navHostController)
        })
}

/**
 * Navigation drawer composable
 */
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun NavigationDrawerComposable(
    drawerState: DrawerState,
    gesturesEnabled: Boolean,
    isOnboarded: Boolean = false,
    activeButtons: Map<MainNavOption, Boolean>? = emptyMap(),
    navHostController: NavHostController,
    currentScreen: MainNavOption? = null,
    onNewCurrentPickChange: (MainNavOption) -> Unit,
    onLogout: (NavHostController) -> Unit
) {
    ModalNavigationDrawer(modifier = Modifier.navigationBarsPadding(),
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            AppDrawerContent(
                drawerState = drawerState,
                menuItems = DrawerParams.createDrawerButtons(activeButtons),
                currentPick = currentScreen
                    ?: MainNavOption.HomeScreen,//defaultPick, //MainNavOption.LoginScreen
                onCurrentPickChange = { newCurrentPick ->
                    onNewCurrentPickChange(newCurrentPick)
                },
            ) { onUserPickedOption ->

                /*
                * When the user picks an option from the drawer, navigate to the corresponding screen,
                * close the drawer and update the current screen in the viewmodel
                 */
                onNewCurrentPickChange(onUserPickedOption)

                when (onUserPickedOption) {
                    MainNavOption.LoginScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.HomeScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.OldHomeScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.HomeScreen.name)
                        }
                    }

                    MainNavOption.CustomersScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.HomeScreen.name)
                        }
                    }

                    MainNavOption.CustomersPagedScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.HomeScreen.name)
                        }
                    }

                    MainNavOption.ArticlesScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.HomeScreen.name)
                        }
                    }

                    MainNavOption.ItemsScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.HomeScreen.name)
                        }
                    }

                    MainNavOption.WarehouseOutOperationsScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.HomeScreen.name)
                        }
                    }

                    MainNavOption.WarehouseInOperationsScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.HomeScreen.name)
                        }
                    }

                    MainNavOption.OrdersScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.HomeScreen.name)
                        }
                    }

                    MainNavOption.SettingsScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.CartScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.HomeScreen.name)
                        }
                    }

                    MainNavOption.AboutScreen -> {
                        navHostController.navigate(onUserPickedOption.name) {
                            popUpTo(MainNavOption.SettingsScreen.name)
                        }
                    }

                    MainNavOption.Logout -> {
                        onLogout(navHostController)
                        //appNavigationViewModel.setCurrentScreen(MainNavOption.LoginScreen)
                    }

//                    else -> {
//                        //appNavigationViewModel.setGesturesEnabled(false)
//                    }
                }
            }
        }) {
        NavHost(
            navHostController, startDestination = if (isOnboarded) {
                NavRoutes.MainRoute.name
            } else NavRoutes.IntroRoute.name
        ) {
            introGraph(navHostController)
            mainGraph(drawerState, navHostController)
        }
    }
}

/**
 * Main navigation graph
 */
enum class NavRoutes {
    /**
     * Intro route
     */
    IntroRoute,

    /**
     * Main route
     */
    MainRoute
}

/**
 * Drawer button parameters
 */
object DrawerParams {

    /**
     * Function to create drawer buttons based on activeButtonsUiState
     */
    fun createDrawerButtons(activeButtonsUiState: Map<MainNavOption, Boolean>?): List<AppDrawerItemInfo<MainNavOption>> {
        val buttons = arrayListOf<AppDrawerItemInfo<MainNavOption>>()

        //NewHome button always visible
        buttons.add(
            AppDrawerItemInfo(
                drawerOption = MainNavOption.HomeScreen,
                title = R.string.drawer_home_menu,
                icon = Icons.Default.Home,
                descriptionId = R.string.drawer_home_description
            )
        )
        if (activeButtonsUiState?.get(MainNavOption.OldHomeScreen) == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.OldHomeScreen,
                    R.string.drawer_home,
                    Icons.Default.Home,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState?.get(MainNavOption.CustomersScreen) == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.CustomersScreen,
                    R.string.drawer_customers,
                    Icons.Default.Person,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState?.get(MainNavOption.CustomersPagedScreen) == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.CustomersPagedScreen,
                    R.string.drawer_customers2,
                    Icons.Default.Person,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState?.get(MainNavOption.ArticlesScreen) == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.ArticlesScreen,
                    R.string.articles,
                    Icons.AutoMirrored.Filled.ListAlt,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState?.get(MainNavOption.ItemsScreen) == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.ItemsScreen,
                    R.string.drawer_inventory,
                    Icons.Default.Folder,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState?.get(MainNavOption.WarehouseOutOperationsScreen) == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.WarehouseOutOperationsScreen,
                    R.string.warehouse_operations,
                    Icons.Default.Folder,
                    R.string.drawer_home_description
                )
            )
        }
        if (activeButtonsUiState?.get(MainNavOption.WarehouseInOperationsScreen) == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.WarehouseInOperationsScreen,
                    R.string.warehouse_operations,
                    Icons.Default.Folder,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState?.get(MainNavOption.OrdersScreen) == true) {
            buttons.add(
                AppDrawerItemInfo(
                    MainNavOption.OrdersScreen,
                    R.string.drawer_orders,
                    Icons.Default.Description,
                    R.string.drawer_home_description
                )
            )
        }

        if (activeButtonsUiState?.get(MainNavOption.CartScreen) == true) {
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

/**
 *  Preview for [MainCompose]
 */
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Preview(apiLevel = 34, showBackground = true)
@Composable
fun MainComposePreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val gesturesEnabled = false
    val activeButtons = null
    val navController = rememberNavController()

    MastroAndroidPreviewTheme {
        NavigationDrawerComposable(drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            activeButtons = activeButtons,
            currentScreen = MainNavOption.HomeScreen,
            navHostController = navController,
            onNewCurrentPickChange = {},
            onLogout = {})
    }
}
