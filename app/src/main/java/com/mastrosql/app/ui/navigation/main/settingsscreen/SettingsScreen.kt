package com.mastrosql.app.ui.navigation.main.settingsscreen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController
) {

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var showDialog by remember { mutableStateOf(false) }
    var baseUrl by remember { mutableStateOf("") }

    val hashMapButtons = remember {
        mutableStateOf(
            hashMapOf<MainNavOption, Boolean>().apply {
                MainNavOption.entries.sortedBy { it.ordinal }.forEach {
                    this[it] = it == MainNavOption.Logout
                }
            }
        )
    }

    //associo le stringe in strings.xml ad ogni nav
    val stringResMap = remember {
        mapOf(
            MainNavOption.CustomersScreen to R.string.drawer_customers,
            MainNavOption.CustomersPagedScreen to R.string.drawer_customers2,
            MainNavOption.ArticlesScreen to R.string.drawer_articles,
            MainNavOption.ItemsScreen to R.string.drawer_inventory,
            MainNavOption.OrdersScreen to R.string.drawer_orders
        )
    }

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = stringResource(R.string.drawer_settings),
                navigateUp = {
                    navController.navigate(MainNavOption.LoginScreen.name) {
                        popUpTo(MainNavOption.LoginScreen.name)
                    }
                }
            )
        },
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = baseUrl,
                    singleLine = true,
                    onValueChange = { baseUrl = it },
                    label = { Text(stringResource(R.string.label_url)) },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                )
                BackHandler(true) { focusManager.clearFocus() }
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Button(onClick = { showDialog = true }) {
                    Text(stringResource(R.string.dialog_button))
                }
            }


            //inizio finestra
            if (showDialog) {
                AlertDialog(
                    modifier = Modifier
                        .size(400.dp)
                        .padding(8.dp),
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(R.string.dialog_button)) },
                    text = {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {

                            items(MainNavOption.entries) { option ->
                                if (
                                    option != MainNavOption.LoginScreen &&
                                    option != MainNavOption.NewHomeScreen &&
                                    option != MainNavOption.HomeScreen &&
                                    option != MainNavOption.SettingsScreen &&
                                    option != MainNavOption.CartScreen &&
                                    option != MainNavOption.Logout
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = stringResource(
                                                stringResMap[option] ?: R.string.label_url
                                            )
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Switch(
                                            checked = hashMapButtons.value[option] ?: false,
                                            onCheckedChange = {
                                                hashMapButtons.value[option] = it
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // gestire salvataggio
                                showDialog = false
                            }
                        ) {
                            Text(stringResource(R.string.confirm_button))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                // gestire annulla salvataggio
                                showDialog = false
                            }
                        ) {
                            Text(stringResource(R.string.cancel_button))
                        }
                    }
                )
            }
            //fine finestra

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.stringa_prova))
                Switch(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End),
                    checked = showDialog,
                    onCheckedChange = { showDialog = it }
                )
            }
        }
    }
}


@Preview(apiLevel = 33)
@Composable
fun SettingsScreenPreview() {
    MastroAndroidTheme {
        SettingsScreen(NavController(LocalContext.current))
    }
}