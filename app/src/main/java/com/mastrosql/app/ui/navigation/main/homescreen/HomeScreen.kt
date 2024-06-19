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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

/**
 * HomeScreen composable to display the home screen of the app.
 */
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navController: NavController,
    viewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    // Get AppNavigationViewModel from LocalAppNavigationViewModelProvider
    val appNavigationViewModel = LocalAppNavigationViewModelProvider.current

    // Collect the state from the view model and update the UI
    val userPreferencesUiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Get active buttons from UserPreferencesViewModel
    val activeButtonsUiState by rememberUpdatedState(userPreferencesUiState.activeButtons)

    // Get the current orientation
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(topBar = {
        AppBar(
            drawerState = drawerState,
            title = R.string.drawer_home_menu,
            showDrawerIconButton = false
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLandscape) {
                MultiColumnButtons(activeButtonsUiState = activeButtonsUiState,
                    navController = navController,
                    appNavigationViewModel = appNavigationViewModel,
                    logout = { viewModel.logout(navController) })
            } else {
                OneColumnButtons(activeButtonsUiState = activeButtonsUiState,
                    navController = navController,
                    appNavigationViewModel = appNavigationViewModel,
                    logout = { viewModel.logout(navController) })
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
                AppButton(modifier = buttonsModifier,
                    text = item.labelResId,
                    onClick = { item.action(navController, appNavigationViewModel) })
            }
        }

        // Logout button always visible
        Spacer(modifier = Modifier.height(64.dp))
        LogoutButton(modifier = buttonsModifier, logout = logout)
    }
}

@Composable
fun MultiColumnButtons(
    activeButtonsUiState: EnumMap<MainNavOption, Boolean>,
    navController: NavController,
    appNavigationViewModel: AppNavigationViewModel,
    logout: () -> Unit = {}
) {
    val buttonsModifier = Modifier.padding(8.dp)

    val filteredButtons =
        buttonItems.filter { it.destination in activeButtonsUiState && activeButtonsUiState[it.destination] == true }

    val chunkSize = 2
    val items = filteredButtons.chunked(chunkSize)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items) { chunk ->
            Row(Modifier.fillMaxWidth()) {
                chunk.forEach { button ->
                    Spacer(Modifier.width(16.dp))
                    AppButton(modifier = buttonsModifier.weight(1f),
                        text = button.labelResId,
                        onClick = { button.action(navController, appNavigationViewModel) })
                }
            }
        }

        // Add a dummy button if the number of buttons + 1 (logout) is odd
        if ((filteredButtons.size + 1) % 2 != 0) {
            item {
                Spacer(Modifier.width(16.dp))
                // You can customize the text of the dummy button as needed
                AppButton(modifier = buttonsModifier,
                    text = R.string.dummy_button,
                    onClick = { /* No action needed */ })
            }
        }
        //Logout button always visible
        item {
            Spacer(Modifier.height(16.dp))
            LogoutButton(modifier = buttonsModifier, logout = logout)
        }
    }
}

@Composable
fun LogoutButton(
    modifier: Modifier, logout: () -> Unit = {}
) {
    AppButton(modifier = modifier, text = R.string.drawer_logout_description, onClick = {
        logout()
    })
}

/**
 * Preview the HomeScreen composable.
 */
@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(
        navController = NavController(LocalContext.current),
        viewModel = viewModel(factory = AppViewModelProvider.Factory)
    )
}

/**
 * List of buttons to be displayed on the home screen.
 */
@Composable
@Preview(showBackground = true)
fun OneColumnButtonsPreview() {
    // Create a mock EnumMap with all buttons set to true
    val activeButtonsUiState: EnumMap<MainNavOption, Boolean> = EnumMap(MainNavOption::class.java)
    MainNavOption.entries.forEach { option ->
        activeButtonsUiState[option] = true
    }
    OneColumnButtons(activeButtonsUiState = activeButtonsUiState,
        navController = NavController(LocalContext.current),
        appNavigationViewModel = AppNavigationViewModel(),
        logout = {})
}

/**
 * Preview the MultiColumnButtons composable.
 */
@Composable
@Preview(showBackground = true, uiMode = Configuration.ORIENTATION_LANDSCAPE)
fun MultiColumnButtonsPreview() {
    // Create a mock EnumMap with all buttons set to true
    val activeButtonsUiState: EnumMap<MainNavOption, Boolean> = EnumMap(MainNavOption::class.java)
    MainNavOption.entries.forEach { option ->
        activeButtonsUiState[option] = true
    }
    MultiColumnButtons(activeButtonsUiState = activeButtonsUiState,
        navController = NavController(LocalContext.current),
        appNavigationViewModel = AppNavigationViewModel(),
        logout = {})
}