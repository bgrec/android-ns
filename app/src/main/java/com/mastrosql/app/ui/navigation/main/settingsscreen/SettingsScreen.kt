package com.mastrosql.app.ui.navigation.main.settingsscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.NavRoutes
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import java.util.EnumMap

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val context = LocalContext.current

    val currentBaseUrlUiState by viewModel.baseUrlUiState.collectAsState()
    val activeButtonsUiState by viewModel.activeButtonsUiState.collectAsState()

    val urlState = remember { mutableStateOf(currentBaseUrlUiState) }

    // Update the local state when the base URL changes
    LaunchedEffect(currentBaseUrlUiState) {
        urlState.value = currentBaseUrlUiState
    }

    SettingsComposable(
        navController = navController,
        activeButtonsUiState = activeButtonsUiState,
        currentBaseUrlState = urlState,
        onSaveUrl = { url -> viewModel.setBaseUrl(url) },
        onSetOnboardingCompleted = { isOnboardingCompleted ->
            viewModel.onBoardingCompleted(
                isOnboardingCompleted
            )
        },
        onUpdateActiveButtons = { updatedState -> viewModel.updateActiveButtons(updatedState) },
        onTestConnection = { viewModel.testRetrofitConnection(context) }
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsComposable(
    navController: NavController,
    activeButtonsUiState: EnumMap<MainNavOption, Boolean>,
    currentBaseUrlState: MutableState<String>,
    onSaveUrl: (String) -> Unit,
    onSetOnboardingCompleted: (Boolean) -> Unit,
    onUpdateActiveButtons: (EnumMap<MainNavOption, Boolean>) -> Unit,
    onTestConnection: () -> Unit

) {

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.drawer_settings),
                navigateUp = {
                    navController.navigate(MainNavOption.LoginScreen.name) {
                        popUpTo(NavRoutes.MainRoute.name)
                    }
                },
                onClick = {
                    navController.navigate(MainNavOption.AboutScreen.name) {
                        popUpTo(MainNavOption.SettingsScreen.name)
                    }
                }
            )
        },
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    //viewModel.setBaseUrl(urlState)
                    if (currentBaseUrlState.value.isNotEmpty()) {
                        onSaveUrl(currentBaseUrlState.value)
                    }
                    focusManager.clearFocus()
                })
            }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BackHandler(true) {
                navController.navigate(MainNavOption.LoginScreen.name) {
                    popUpTo(NavRoutes.MainRoute.name)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                val url by rememberSaveable { mutableStateOf(currentBaseUrlState) }
                val updatedUrlState by rememberUpdatedState(url.value)

                OutlinedTextField(
                    value = url.value,
                    singleLine = false,
                    onValueChange = {
                        url.value = it
                    },
                    leadingIcon = {
                        Icon(
                            painterResource(R.drawable.bring_your_own_ip),
                            null
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            //viewModel.setBaseUrl(urlState)
                            onSaveUrl(updatedUrlState)
                            focusManager.clearFocus()
                        }
                    ),
                    label = { Text(stringResource(R.string.label_url)) },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                )

            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(64.dp, 8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDialog.value = true }) {
                    Text(stringResource(R.string.dialog_button_settings))
                }
            }

            if (showDialog.value) {
                ButtonActivationDialog(
                    showDialog = showDialog,
                    activeButtonsUiState = activeButtonsUiState,
                    onUpdateActiveButtons = onUpdateActiveButtons
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(64.dp, 8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSetOnboardingCompleted(false)
                        //viewModel.onBoardingCompleted(false)
                    }
                ) {
                    Text(text = stringResource(R.string.show_intro_again))
                }
            }
            Spacer(modifier = Modifier)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(64.dp, 8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /*viewModel.testRetrofitConnection(context)*/ }
                ) {
                    Text(text = stringResource(R.string.private_webserver))
                }
            }

            Spacer(modifier = Modifier)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(64.dp, 8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /*viewModel.testRetrofitConnection(context)*/ }
                ) {
                    Text(text = stringResource(R.string.delete_permission_menu))
                }
            }

            Spacer(modifier = Modifier)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(64.dp, 8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onTestConnection() }
                ) {
                    Text(text = stringResource(R.string.test_retrofit_button))
                }
            }
        }
    }
}

@Composable
fun ButtonActivationDialog(
    showDialog: MutableState<Boolean>,
    activeButtonsUiState: EnumMap<MainNavOption, Boolean>,
    onUpdateActiveButtons: (EnumMap<MainNavOption, Boolean>) -> Unit
) {
    val stringResMap by remember {
        mutableStateOf(
            mapOf(
                MainNavOption.CustomersScreen to R.string.drawer_customers,
                MainNavOption.CustomersPagedScreen to R.string.drawer_customers2,
                MainNavOption.ArticlesScreen to R.string.drawer_articles,
                MainNavOption.ItemsScreen to R.string.drawer_inventory,
                MainNavOption.OrdersScreen to R.string.drawer_orders
            )
        )
    }

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(stringResource(R.string.dialog_button_settings)) },
        text = {
            LazyColumn(modifier = Modifier.wrapContentSize()) {
                items(MainNavOption.entries.toList()) {
                    if ((stringResMap[it] != null)) {
                        Row(
                            modifier = Modifier.clickable(
                                onClick = {
                                    val isChecked = !(activeButtonsUiState[it] ?: false)
                                    val updatedState = EnumMap(activeButtonsUiState)
                                    updatedState[it] = isChecked
                                    //viewModel.updateActiveButtons(updatedState)
                                    onUpdateActiveButtons(updatedState)
                                }
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(stringResMap[it] ?: R.string.null_text))

                            Spacer(Modifier.weight(1f))

                            Switch(
                                checked = activeButtonsUiState[it] ?: false,
                                onCheckedChange = { isChecked ->
                                    val updatedState = EnumMap(activeButtonsUiState)
                                    updatedState[it] = isChecked

                                    onUpdateActiveButtons(updatedState)
                                })
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                showDialog.value = false
            }) {
                Text(stringResource(R.string.close_button))
            }
        },
        modifier = Modifier.wrapContentSize()
    )
}

@Preview(showBackground = true)
@Composable
fun ButtonsActivationDialogPreview(
) {
    MastroAndroidTheme {
        ButtonActivationDialog(
            showDialog = remember { mutableStateOf(true) },
            activeButtonsUiState = EnumMap(MainNavOption::class.java),
            onUpdateActiveButtons = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MastroAndroidTheme {
        SettingsComposable(
            navController = NavController(LocalContext.current),
            activeButtonsUiState = EnumMap(MainNavOption::class.java),
            currentBaseUrlState = remember { mutableStateOf("https://example.com/api") },
            onSaveUrl = {},
            onSetOnboardingCompleted = {},
            onUpdateActiveButtons = {},
            onTestConnection = {}
        )
    }
}
