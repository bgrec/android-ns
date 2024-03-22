package com.mastrosql.app.ui.navigation.main.loginscreen

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.AppButton
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.theme.MastroAndroidTheme


@Composable
fun LoginScreen(
    navController: NavController,
    //viewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory)
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    var isFirstClick by remember { mutableStateOf(true) }

    //Initialize the CredentialManager in the ViewModel with the context
    viewModel.initCredentialManager(context)

    Scaffold(
        topBar = {
            LoginAppBar(onClick = {
                navController.navigate(MainNavOption.SettingsScreen.name) {
                    // Configure the navigation action
                    popUpTo(MainNavOption.LoginScreen.name)
                }
            })
        },
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(innerPadding),
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
                    keyboardAction = KeyboardActions(),
                    interactionSource = interactionSource
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release && isFirstClick) {
                                        isFirstClick = false
                                        viewModel.getCredentials(context)
                                    }
                                }
                            }
                        }
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

                    ),
                    keyboardAction = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.login(
                                context = context,
                                username = username,
                                password = password,
                                isCredentialManagerLogin = false
                            )
                        }
                    ),
                )
                //Spacer(modifier = Modifier.height(50.dp))
                Spacer(modifier = Modifier.weight(1f))

                AppButton(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .align(Alignment.CenterHorizontally),
                    text = R.string.login,
                    onClick = {
                        viewModel.login(
                            context = context,
                            username = username,
                            password = password,
                            isCredentialManagerLogin = false
                        )
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
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

@Composable
fun LoginFields(
    @StringRes label: Int,
    value: String,
    isPassword: Boolean = false,
    icon: @Composable (() -> Unit),
    onValueChanged: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardAction: KeyboardActions,
    modifier: Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
) {

    val focusRequester = remember { FocusRequester() }
    //val focusManager = LocalFocusManager.current

    OutlinedTextField(
        leadingIcon = icon,
        value = value,
        singleLine = true,
        onValueChange = onValueChanged,
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardAction,
        modifier = modifier
            .focusRequester(focusRequester),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else visualTransformation,
        interactionSource = interactionSource
    )
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
                    keyboardAction = KeyboardActions(
                        onNext = {
                            //focusManager.clearFocus()
                        }
                    )
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
                    ),
                    keyboardAction = KeyboardActions(
                        onDone = {
                            //focusManager.clearFocus()
                        }
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