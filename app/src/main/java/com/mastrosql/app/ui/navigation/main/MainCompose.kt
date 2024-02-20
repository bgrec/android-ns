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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    viewModel: IntroViewModel = viewModel(factory = AppViewModelProvider.Factory)//hiltViewModel()
) {
    var gesturesEnabled by remember { mutableStateOf(true) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            AppDrawerContent(
                drawerState = drawerState,
                menuItems = DrawerParams.drawerButtons,
                defaultPick = MainNavOption.LoginScreen
            ) { onUserPickedOption ->
                when (onUserPickedOption) {
                    MainNavOption.LoginScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                            gesturesEnabled = false
                        }
                    }

                    MainNavOption.NewHomeScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.HomeScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.CustomersScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.CustomersPagedScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.ArticlesScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.ItemsScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.OrdersScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.SettingsScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    /*MainNavOption.AboutScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }*/

                    MainNavOption.CartScreen -> {
                        navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                        }
                    }

                    MainNavOption.Logout -> {
                        // Handle logout
                        viewModel.logoutUser()
                    }


                }
            }
        }
    ) {
        // call the navigation graph
        val isOnboarded = viewModel.isOnboarded.collectAsState()
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
    MainRoute,
}

object DrawerParams {
    val drawerButtons = arrayListOf(
        AppDrawerItemInfo(
            MainNavOption.LoginScreen,
            R.string.drawer_login,
            Icons.AutoMirrored.Filled.Login,
            R.string.drawer_login_description
        ),
        AppDrawerItemInfo(
            MainNavOption.NewHomeScreen,
            R.string.drawer_new_home,
            Icons.Default.Home,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.HomeScreen,
            R.string.drawer_home,
            Icons.Default.Home,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.CustomersScreen,
            R.string.drawer_customers,
            Icons.Default.Person,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.CustomersPagedScreen,
            R.string.drawer_customers2,
            Icons.Default.Person,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.ArticlesScreen,
            R.string.articles,
            Icons.AutoMirrored.Filled.ListAlt,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.ItemsScreen,
            R.string.drawer_inventory,
            Icons.Default.Folder,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.OrdersScreen,
            R.string.drawer_orders,
            Icons.Default.Description,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.SettingsScreen,
            R.string.drawer_settings,
            Icons.Default.Settings,
            R.string.drawer_settings_description
        ),
        //TODO: Remove AboutScreen because it was moved to the intro navigation graph
        /*
        AppDrawerItemInfo(
            MainNavOption.AboutScreen,
            R.string.drawer_about,
            R.drawable.ic_info,
            R.string.drawer_info_description
        ),*/
        AppDrawerItemInfo(
            MainNavOption.CartScreen,
            R.string.drawer_cart,
            Icons.Default.ShoppingCart,
            R.string.drawer_cart_description
        ),
        AppDrawerItemInfo(
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