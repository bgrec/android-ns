package com.mastrosql.app.ui.navigation.main.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.AppButton
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.navigation.AppNavigationViewModel
import com.mastrosql.app.ui.navigation.LocalAppNavigationViewModelProvider
import com.mastrosql.app.ui.navigation.main.settingsscreen.UserPreferencesViewModel
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.loginscreen.LogoImage
import java.util.EnumMap

@Composable
fun NewHomeScreen(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navController: NavController,
    preferencesViewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Get AppNavigationViewModel from LocalAppNavigationViewModelProvider
    val appNavigationViewModel = LocalAppNavigationViewModelProvider.current

    // Get active buttons from UserPreferencesViewModel
    val activeButtonsUiState by preferencesViewModel.activeButtonsUiState.collectAsState()

    Scaffold(
        topBar = {
            AppBar(
                drawerState = drawerState,
                title = R.string.drawer_home_menu,
                showDrawerIconButton = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CenteredLogoImage()
            HomeButtons(
                activeButtonsUiState,
                navController,
                appNavigationViewModel,
                logout = {
                    preferencesViewModel.logout(navController)
                }
            )
        }
    }
}

@Composable
fun CenteredLogoImage() {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LogoImage()
    }
}

@Composable
fun HomeButtons(
    activeButtonsUiState: EnumMap<MainNavOption, Boolean>,
    navController: NavController,
    appNavigationViewModel: AppNavigationViewModel,
    logout: () -> Unit = {}
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        val buttonsModifier = Modifier
            .width(300.dp)
            .height(50.dp)
            .align(Alignment.CenterHorizontally)

        if (activeButtonsUiState[MainNavOption.CustomersScreen] == true) {
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

        // Logout button always visible
        Spacer(modifier = Modifier.height(10.dp))
        AppButton(
            modifier = buttonsModifier,
            text = R.string.drawer_logout_description,
            onClick = {
                logout()
            })
    }
}

@Composable
@Preview
fun CenteredLogoImagePreview() {
    CenteredLogoImage()
}

//@Preview(apiLevel = 33)
//@Composable
//fun NewHomeScreenPreview(
//) {
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val navController = NavController(LocalContext.current)
//    NewHomeScreen(drawerState, navController)
//}


//@Composable
//@Preview(apiLevel = 33)
//fun HomeButtonsPreview() {
//    val navController = rememberNavController()
//
//    // Create a mock EnumMap to simulate active buttons state
//    val activeButtonsUiState: EnumMap<MainNavOption, Boolean> = EnumMap(MainNavOption::class.java)
//    activeButtonsUiState[MainNavOption.CustomersScreen] = true
//    activeButtonsUiState[MainNavOption.CustomersPagedScreen] = true
//    activeButtonsUiState[MainNavOption.ArticlesScreen] = true
//    activeButtonsUiState[MainNavOption.ItemsScreen] = true
//    activeButtonsUiState[MainNavOption.OrdersScreen] = true
//
//    val appNavigationViewModel = LocalAppNavigationViewModelProvider.current
//    // Provide the mock AppNavigationViewModel via CompositionLocal
//    CompositionLocalProvider(LocalAppNavigationViewModelProvider provides appNavigationViewModel) {
//        HomeButtons(activeButtonsUiState, navController, appNavigationViewModel)
//    }
//}