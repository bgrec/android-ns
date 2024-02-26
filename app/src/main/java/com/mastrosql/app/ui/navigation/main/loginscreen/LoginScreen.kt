package com.mastrosql.app.ui.navigation.main.loginscreen

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.components.AppButton
import com.mastrosql.app.ui.navigation.main.AppNavigationViewModel
import com.mastrosql.app.ui.navigation.main.LocalAppNavigationViewModelProvider
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.NavRoutes
import com.mastrosql.app.ui.theme.MastroAndroidTheme


@Composable
fun LoginScreen(
    navController: NavController //= rememberNavController(),
) {
    val gestureViewModel: AppNavigationViewModel = LocalAppNavigationViewModelProvider.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            LoginAppBar(onClick = {
                navController.navigate(MainNavOption.SettingsScreen.name) {
                    // Configure the navigation action
                    popUpTo(NavRoutes.MainRoute.name) {
                        inclusive =
                            true // Set to true if you want to include MainRoute in the popUpTo destination
                    }
                }
            })
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
                .padding(24.dp)
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.offset(y = 50.dp)
            )
            {
                LogoImage()
            }// Show the logo in the center below the TopAppBar

            Spacer(modifier = Modifier.height(16.dp)) // Add some space between the logo and the text fields

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                // TextField for Username
                Spacer(modifier = Modifier.weight(1f))

                LoginFields(
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .align(Alignment.CenterHorizontally),
                    label = R.string.Username,
                    value = username,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = null
                        )
                    },
                    onValueChanged = { username = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                )

                //Spacer(modifier = Modifier.height(30.dp)) // Add some space between the text fields
                Spacer(modifier = Modifier.weight(0.1f))
                // TextField for Password
                LoginFields(
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .align(Alignment.CenterHorizontally),
                    isPassword = true,
                    label = R.string.Password,
                    value = password,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null
                        )
                    },
                    onValueChanged = { password = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )
                //Spacer(modifier = Modifier.height(50.dp))
                Spacer(modifier = Modifier.weight(1f))

                AppButton(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .align(Alignment.CenterHorizontally),
                    text = R.string.login,
                    onClick = {
                        // Handle login button click here
                        navController.navigate(MainNavOption.NewHomeScreen.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                            //gestureViewModel.setGesturesEnabled(false)
                        }

                        /*navController.navigate(onUserPickedOption.name) {
                            popUpTo(NavRoutes.MainRoute.name)
                            gestureViewModel.setGesturesEnabled(true)                       }
                    }*/

                    }
                )
                /*LoginButton(onClick = {
                    //navController.navigate(MainNavOption.HomeScreen.name)
                    navController.navigate(MainNavOption.CustomersScreen.name)
                }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    // Handle login button click here
                }*/

                Spacer(modifier = Modifier.weight(1f))
                // ModalNavigationDrawerSample(drawerState = drawerState, scope = scope){
                //}
            }
        }
    }
}


@Composable
fun LogoImage() {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "Logo Nipeservice",
        modifier = Modifier.size(150.dp)
    )
}

/*@Composable
fun LoginButton(onClick: () -> Unit, modifier: Modifier, function: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text("Login")
    }
}*/

@Composable
fun LoginFields(
    @StringRes label: Int,
    value: String,
    isPassword: Boolean = false,
    icon: @Composable (() -> Unit),
    onValueChanged: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        leadingIcon = icon,
        value = value,
        singleLine = true,
        onValueChange = onValueChanged,
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .focusRequester(focusRequester),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else visualTransformation
    )
    BackHandler(true) { focusManager.clearFocus() }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
fun LoginScreenPreview() {
    MastroAndroidTheme {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.offset(y = 50.dp)
            )
            {
                LogoImage()
            }// Show the logo in the center below the TopAppBar

            Spacer(modifier = Modifier.height(16.dp)) // Add some space between the logo and the text fields

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                // TextField for Username
                Spacer(modifier = Modifier.weight(1f))

                LoginFields(
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .align(Alignment.CenterHorizontally),
                    label = R.string.Username,
                    value = username,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = null
                        )
                    },
                    onValueChanged = { username = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                )

                //Spacer(modifier = Modifier.height(30.dp)) // Add some space between the text fields
                Spacer(modifier = Modifier.weight(0.1f))


                LoginFields(
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .align(Alignment.CenterHorizontally),
                    isPassword = true,
                    label = R.string.Password,
                    value = password,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null
                        )
                    },
                    onValueChanged = { password = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )
                //Spacer(modifier = Modifier.height(50.dp))
                Spacer(modifier = Modifier.weight(1f))

                AppButton(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .align(Alignment.CenterHorizontally),
                    text = R.string.login,
                    onClick = { println("prova") }

                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}