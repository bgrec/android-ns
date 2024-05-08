package com.mastrosql.app.ui.navigation.main.homescreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.AppButton
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.navigation.AppNavigationViewModel
import com.mastrosql.app.ui.navigation.LocalAppNavigationViewModelProvider
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.homescreen.ButtonItemsList.buttonItems
import com.mastrosql.app.ui.navigation.main.loginscreen.LogoImage
import com.mastrosql.app.ui.navigation.main.settingsscreen.UserPreferencesViewModel
import java.util.EnumMap

@Composable
fun HomeScreen(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navController: NavController,
    preferencesViewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    // Get AppNavigationViewModel from LocalAppNavigationViewModelProvider
    val appNavigationViewModel = LocalAppNavigationViewModelProvider.current

    // Get active buttons from UserPreferencesViewModel
    val activeButtonsUiState by rememberUpdatedState(preferencesViewModel.activeButtonsUiState.collectAsState())

    // Get the current orientation
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

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
            if (isLandscape) {
                MultiColumnButtons(
                    activeButtonsUiState = activeButtonsUiState.value,
                    navController = navController,
                    appNavigationViewModel = appNavigationViewModel,
                    logout = { preferencesViewModel.logout(navController) }
                )
            } else {
                OneColumnButtons(
                    activeButtonsUiState = activeButtonsUiState.value,
                    navController = navController,
                    appNavigationViewModel = appNavigationViewModel,
                    logout = { preferencesViewModel.logout(navController) }
                )
            }
        }
    }
}

@Composable
fun OneColumnButtons(
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

        LogoImage()
        Spacer(modifier = Modifier.height(32.dp))

        buttonItems.forEach { item ->
            if (item.destination in activeButtonsUiState && activeButtonsUiState[item.destination] == true) {
                Spacer(Modifier.height(16.dp))
                AppButton(
                    modifier = buttonsModifier,
                    text = item.labelResId,
                    onClick = { item.action(navController, appNavigationViewModel) }
                )
            }
        }

        // Logout button always visible
        Spacer(modifier = Modifier.height(64.dp))
        AppButton(
            modifier = buttonsModifier,
            text = R.string.drawer_logout_description,
            onClick = {
                logout()
            })
    }
}

@Composable
fun MultiColumnButtons(
    activeButtonsUiState: EnumMap<MainNavOption, Boolean>,
    navController: NavController,
    appNavigationViewModel: AppNavigationViewModel,
    logout: () -> Unit = {}
) {
    val buttonsModifier = Modifier
        //.width(300.dp)
        .height(50.dp)

    val filteredButtons = buttonItems
        .filter { it.destination in activeButtonsUiState && activeButtonsUiState[it.destination] == true }

    val chunkSize = 3
    val items = filteredButtons.chunked(chunkSize)

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items) { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { button ->
                    Spacer(Modifier.width(16.dp))
                    AppButton(
                        modifier = buttonsModifier
                            .padding(8.dp)
                            .weight(1f),
                        text = button.labelResId,
                        onClick = { button.action(navController, appNavigationViewModel) }
                    )
                }
            }
        }

        //Logout button always visible
        item {
            Spacer(Modifier.height(16.dp))
            AppButton(
                modifier = buttonsModifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                text = R.string.drawer_logout_description,
                onClick = {
                    logout()
                }
            )
        }
    }
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