package com.mastrosql.app.ui.navigation.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    ModalNavigationDrawer(
        drawerState = drawerState,
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
            android.R.drawable.ic_lock_idle_lock,
            R.string.drawer_login_description
        ),
        AppDrawerItemInfo(
            MainNavOption.HomeScreen,
            R.string.drawer_home,
            R.drawable.ic_home,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.CustomersScreen,
            R.string.drawer_customers,
            android.R.drawable.ic_menu_myplaces,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.CustomersPagedScreen,
            R.string.drawer_customers,
            android.R.drawable.ic_menu_myplaces,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.ArticlesScreen,
            R.string.articles,
            android.R.drawable.ic_menu_agenda,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.ItemsScreen,
            R.string.items,
            android.R.drawable.ic_menu_agenda,
            R.string.drawer_home_description
        ),
        AppDrawerItemInfo(
            MainNavOption.SettingsScreen,
            R.string.drawer_settings,
            R.drawable.ic_settings,
            R.string.drawer_settings_description
        ),
        /*AppDrawerItemInfo(
            MainNavOption.AboutScreen,
            R.string.drawer_about,
            R.drawable.ic_info,
            R.string.drawer_info_description
        ),*/
        AppDrawerItemInfo(
            MainNavOption.CartScreen,
            R.string.drawer_cart,
            R.drawable.ic_info,
            R.string.drawer_cart_description
        ),
        AppDrawerItemInfo(
            MainNavOption.Logout,
            R.string.logout,
            R.drawable.ic_info,
            R.string.drawer_logout_description
        )
    )
}

@Preview
@Composable
fun MainActivityPreview() {
    MainCompose()
}