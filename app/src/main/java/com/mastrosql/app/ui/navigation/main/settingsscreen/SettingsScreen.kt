package com.mastrosql.app.ui.navigation.main.settingsscreen

import android.content.res.Configuration
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.NavRoutes
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import kotlinx.coroutines.launch
import java.util.EnumMap

private const val PRIMARY_URL = 0
private const val SECONDARY_URL = 1

/**
 * Settings screen for the app
 */
@ExperimentalMaterial3Api
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Get the context
    val context = LocalContext.current

    // Remember the coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Collect the state from the view model and update the UI
    val userPreferencesUiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Local state to hold the current base URL
    val urlState = remember { mutableStateOf(userPreferencesUiState.baseUrl) }
    val urlNameState = remember { mutableStateOf(userPreferencesUiState.baseUrlName) }

    // Local state to hold the current base URL
    val url2State = remember { mutableStateOf(userPreferencesUiState.baseUrl2) }
    val url2NameState = remember { mutableStateOf(userPreferencesUiState.baseUrl2Name) }

    // Update the local state when the base URL changes
    LaunchedEffect(userPreferencesUiState.baseUrl) {
        urlState.value = userPreferencesUiState.baseUrl
        urlNameState.value = userPreferencesUiState.baseUrlName
    }

    // Update the local state when the base URL2 changes
    LaunchedEffect(userPreferencesUiState.baseUrl2) {
        url2State.value = userPreferencesUiState.baseUrl2
        url2NameState.value = userPreferencesUiState.baseUrl2Name
    }

    // Composable for the settings screen
    SettingsComposable(navController = navController,
        activeButtonsState = userPreferencesUiState.activeButtons,
        currentBaseUrlState = urlState,
        currentBaseUrlNameState = urlNameState,
        currentBaseUrl2State = url2State,
        currentBaseUrl2NameState = url2NameState,
        isNotSecuredApiState = userPreferencesUiState.isNotSecuredApi,
        isSwipeToDeleteDisabledState = userPreferencesUiState.isSwipeToDeleteDisabled,
        isSwipeToDuplicateDisabledState = userPreferencesUiState.isSwipeToDuplicateDisabled,
        onSaveUrl = { primaryOrSecondary, url, name ->
            if (primaryOrSecondary == PRIMARY_URL) {
                viewModel.setPrimaryBaseUrl(url)
                viewModel.setPrimaryBaseUrlName(name)
            } else {
                viewModel.setSecondaryBaseUrl(url)
                viewModel.setSecondaryBaseUrlName(name)
            }
        },
        onSetOnboardingCompleted = { isOnboardingCompleted ->
            viewModel.onBoardingCompleted(
                isOnboardingCompleted
            )
        },
        onUpdateActiveButtons = { updatedState -> viewModel.updateActiveButtons(updatedState) },
        onTestConnection = {
            coroutineScope.launch {
                viewModel.testRetrofitConnection(context)
            }
        },
        onSetNotSecuredApi = { isNotSecuredApi -> viewModel.setNotSecuredApi(isNotSecuredApi) },
        onSetSwipeToDeleteDisabled = { isSwipeToDeleteDisabled ->
            viewModel.setSwipeToDelete(
                isSwipeToDeleteDisabled
            )
        },
        onSetSwipeToDuplicateDisabled = { isSwipeToDuplicateDisabled ->
            viewModel.setSwipeToDuplicate(
                isSwipeToDuplicateDisabled
            )
        })
}

/**
 * Composable for the settings screen
 */
@ExperimentalMaterial3Api
@Composable
fun SettingsComposable(
    navController: NavController,
    activeButtonsState: EnumMap<MainNavOption, Boolean>,
    currentBaseUrlState: MutableState<String>,
    currentBaseUrlNameState: MutableState<String>,
    currentBaseUrl2State: MutableState<String>,
    currentBaseUrl2NameState: MutableState<String>,
    isNotSecuredApiState: Boolean,
    isSwipeToDeleteDisabledState: Boolean = false,
    isSwipeToDuplicateDisabledState: Boolean = false,
    onSaveUrl: (Int, String, String) -> Unit,
    onSetOnboardingCompleted: (Boolean) -> Unit,
    onUpdateActiveButtons: (EnumMap<MainNavOption, Boolean>) -> Unit,
    onTestConnection: () -> Unit,
    onSetNotSecuredApi: (Boolean) -> Unit,
    onSetSwipeToDeleteDisabled: (Boolean) -> Unit,
    onSetSwipeToDuplicateDisabled: (Boolean) -> Unit
) {

    // Focus requester and manager
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Show the dialog to activate the buttons
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(topBar = {
        SettingsTopAppBar(title = stringResource(R.string.drawer_settings), navigateUp = {
            navController.navigate(MainNavOption.LoginScreen.name) {
                popUpTo(NavRoutes.MainRoute.name)
            }
        }, onClick = {
            navController.navigate(MainNavOption.AboutScreen.name) {
                popUpTo(MainNavOption.SettingsScreen.name)
            }
        })
    }, modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {

            if (currentBaseUrlState.value.isNotEmpty()) {
                onSaveUrl(PRIMARY_URL, currentBaseUrlState.value, currentBaseUrlNameState.value)
            }
            if (currentBaseUrl2State.value.isNotEmpty()) {
                onSaveUrl(SECONDARY_URL, currentBaseUrl2State.value, currentBaseUrl2NameState.value)
            }
            focusManager.clearFocus()
        })
    }) { innerPadding ->

        val isLandscape =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(
                    if (isLandscape) Modifier.verticalScroll(rememberScrollState()) else Modifier
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                BackHandler(true) {
                    navController.navigate(MainNavOption.LoginScreen.name) {
                        popUpTo(NavRoutes.MainRoute.name)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        val url by rememberSaveable { mutableStateOf(currentBaseUrlState) }
                        val updatedUrlState by rememberUpdatedState(url.value)

                        val urlName by rememberSaveable { mutableStateOf(currentBaseUrlNameState) }
                        val updatedUrlNameState by rememberUpdatedState(urlName.value)

                        Column(
                            modifier = Modifier
//                            .weight(1f)
//                            .padding(16.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
//                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextField(
                                    modifier = Modifier.focusRequester(focusRequester),
                                    value = urlName.value,
                                    onValueChange = {
                                        urlName.value = it
                                    },
                                    label = { Text(stringResource(R.string.label_url_name)) },
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
//                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                //Primary URL
                                OutlinedTextField(
                                    value = url.value,
                                    singleLine = false,
                                    onValueChange = {
                                        url.value = it
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painterResource(R.drawable.bring_your_own_ip),
                                            contentDescription = stringResource(R.string.label_url)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = {
                                        // Update the URL
                                        onSaveUrl(PRIMARY_URL, updatedUrlState, updatedUrlNameState)
                                        focusManager.clearFocus()
                                    }),
                                    label = { Text(stringResource(R.string.label_url)) },
                                    modifier = Modifier.focusRequester(focusRequester)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(8.dp))

                        Column(
                            modifier = Modifier
//                            .weight(1f)
//                            .padding(16.dp)
                        ) {
                            // Secondary URL
                            val url2 by rememberSaveable { mutableStateOf(currentBaseUrl2State) }
                            val updatedUrl2State by rememberUpdatedState(url2.value)
                            val url2Name by rememberSaveable {
                                mutableStateOf(
                                    currentBaseUrl2NameState
                                )
                            }
                            val updatedUrl2NameState by rememberUpdatedState(url2Name.value)

                            TextField(
                                modifier = Modifier.focusRequester(focusRequester),
                                value = url2Name.value,
                                onValueChange = {
                                    url2Name.value = it
                                },
                                label = { Text(stringResource(R.string.label_url2_name)) },
                            )

                            OutlinedTextField(value = url2.value,
                                singleLine = false,
                                onValueChange = {
                                    url2.value = it
                                },
                                leadingIcon = {
                                    Icon(
                                        painterResource(R.drawable.bring_your_own_ip),
                                        contentDescription = stringResource(R.string.label_url2)
                                    )
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    // Update the URL
                                    onSaveUrl(SECONDARY_URL, updatedUrl2State, updatedUrl2NameState)
                                    focusManager.clearFocus()
                                }),
                                label = { Text(stringResource(R.string.label_url2)) },
                                modifier = Modifier.focusRequester(focusRequester)
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider()
            }


            item {
                val rowModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = rowModifier
                ) {
                    Text(
                        text = stringResource(R.string.private_webserver),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = isNotSecuredApiState, onCheckedChange = { isChecked ->
                        onSetNotSecuredApi(isChecked)
                    })
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = rowModifier
                ) {
                    Text(
                        text = stringResource(R.string.delete_row_disabled),
                        modifier = Modifier.weight(1f)
                    )

                    Switch(checked = isSwipeToDeleteDisabledState, onCheckedChange = { isChecked ->
                        onSetSwipeToDeleteDisabled(isChecked)
                    })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = rowModifier
                ) {
                    Text(
                        text = stringResource(R.string.duplicate_row_disabled),
                        modifier = Modifier.weight(1f)
                    )

                    Switch(checked = isSwipeToDuplicateDisabledState,
                        onCheckedChange = { isChecked ->
                            onSetSwipeToDuplicateDisabled(isChecked)
                        })
                }
            }

            item {
                // Activate the buttons, show intro again, and close the dialog
                HorizontalDivider()
            }
            item {
                Spacer(modifier = Modifier.padding(8.dp))
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Button(modifier = Modifier.weight(0.45f),
                        onClick = { showDialog.value = true }) {
                        Text(stringResource(R.string.dialog_button_settings))
                    }
                    Spacer(modifier = Modifier.weight(0.1f))
                    Button(modifier = Modifier.weight(0.45f), onClick = {
                        onSetOnboardingCompleted(false)
                    }) {
                        Text(text = stringResource(R.string.show_intro_again))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.padding(8.dp))
            }

            // Show the dialog to activate the buttons
            if (showDialog.value) {
                item {
                    MenuButtonsActivationDialog(
                        showDialog = showDialog,
                        activeButtonsUiState = activeButtonsState,
                        onUpdateActiveButtons = onUpdateActiveButtons
                    )
                }
            }

            // Test the connection
            item {
                HorizontalDivider()
            }

            item {
                Spacer(modifier = Modifier.padding(8.dp))
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = { onTestConnection() }) {
                        Text(text = stringResource(R.string.test_retrofit_button))
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.padding(16.dp))
            }

            item {
                HorizontalDivider()
            }

        }
    }
}

/**
 * Top app bar for the settings screen
 */
@Composable
fun MenuButtonsActivationDialog(
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
                MainNavOption.WarehouseOperationsScreen to R.string.warehouse_operations,
                MainNavOption.ItemsScreen to R.string.drawer_inventory,
                MainNavOption.OrdersScreen to R.string.drawer_orders
            )
        )
    }

    AlertDialog(onDismissRequest = { showDialog.value = false },
        title = { Text(stringResource(R.string.dialog_button_settings)) },
        text = {
            LazyColumn(modifier = Modifier.wrapContentSize()) {
                items(MainNavOption.entries.toList()) {
                    if ((stringResMap[it] != null)) {
                        Row(
                            modifier = Modifier.clickable(onClick = {
                                val isChecked = !(activeButtonsUiState[it] ?: false)
                                val updatedState = EnumMap(activeButtonsUiState)
                                updatedState[it] = isChecked

                                // Update the state
                                onUpdateActiveButtons(updatedState)
                            }),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(stringResMap[it] ?: R.string.null_text))

                            Spacer(Modifier.weight(1f))

                            Switch(checked = activeButtonsUiState[it] ?: false,
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

/**
 * Preview for the buttons activation dialog
 */
@Preview(showBackground = true)
@Composable
fun ButtonsActivationDialogPreview(
) {
    MastroAndroidTheme {
        MenuButtonsActivationDialog(showDialog = remember { mutableStateOf(true) },
            activeButtonsUiState = EnumMap(MainNavOption::class.java),
            onUpdateActiveButtons = {})
    }
}

/**
 * Preview for the settings screen
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MastroAndroidTheme {
        SettingsComposable(navController = NavController(LocalContext.current),
            activeButtonsState = EnumMap(MainNavOption::class.java),
            currentBaseUrlState = remember { mutableStateOf("https://example.com/api") },
            currentBaseUrlNameState = remember { mutableStateOf("Primary URL") },
            currentBaseUrl2State = remember { mutableStateOf("https://example.com/api2") },
            currentBaseUrl2NameState = remember { mutableStateOf("Secondary URL") },
            isNotSecuredApiState = false,
            isSwipeToDeleteDisabledState = true,
            onSaveUrl = { _, _, _ -> },
            onSetOnboardingCompleted = {},
            onUpdateActiveButtons = {},
            onTestConnection = {},
            onSetNotSecuredApi = {},
            onSetSwipeToDeleteDisabled = {},
            onSetSwipeToDuplicateDisabled = {})
    }
}
