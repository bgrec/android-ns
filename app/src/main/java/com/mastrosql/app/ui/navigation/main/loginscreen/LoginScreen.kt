package com.mastrosql.app.ui.navigation.main.loginscreen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.AppButton
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import kotlinx.coroutines.launch

/**
 * Composable for the LoginScreen
 */
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    //Context
    val context = LocalContext.current

    //CoroutineScope to launch the CredentialManager
    val coroutineScope = rememberCoroutineScope()

    // Collect the state from the view model and update the UI
    val loginUiState by viewModel.uiState.collectAsStateWithLifecycle()

    //InteractionSource for the TextField to get the credentials from the CredentialManager
    //when the user taps on the TextField
    val interactionSource = remember { MutableInteractionSource() }

    //Initialize the CredentialManager in the ViewModel with the context
    LaunchedEffect(viewModel) {
        coroutineScope.launch {
            viewModel.initCredentialManager(context)
        }
    }

    //Collect the interactions on the TextField to get the credentials from the CredentialManager
    LaunchedEffect(interactionSource) {
        coroutineScope.launch {
            var clickCount = 0
            interactionSource.interactions.collect {
                if (it is PressInteraction.Release && clickCount < 2) {
                    clickCount++
                    viewModel.getCredentials(context)
                }
            }
        }
    }

    Login(
        navController = navController,
        interactionSource = interactionSource,
        onLogin = { username, password ->
            viewModel.login(
                context = context,
                username = username,
                password = password,
                isCredentialManagerLogin = false
            )
        },
        isSecondaryBaseUrlProvided = loginUiState.isSecondaryBaseUrlProvided,
        isNotSecuredApi = loginUiState.isNotSecuredApi,
        selectedUrl = loginUiState.selectedUrl,
        baseUrlName = loginUiState.baseUrlName,
        baseUrl2Name = loginUiState.baseUrl2Name
    )
}

/**
 * Composable Login
 */
@Composable
fun Login(
    navController: NavController,
    interactionSource: MutableInteractionSource,
    onLogin: (String, String) -> Unit,
    isSecondaryBaseUrlProvided: Boolean,
    isNotSecuredApi: Boolean,
    selectedUrl: Int,
    baseUrlName: String,
    baseUrl2Name: String
) {

    //State for the TextField
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    //FocusManager to clear the focus on tap
    val focusManager = LocalFocusManager.current

    //FocusRequester to get the focus on the TextField on opening the screen
    val focusRequester = remember { FocusRequester() }

    Scaffold(topBar = {
        LoginAppBar(onClick = {
            navController.navigate(MainNavOption.SettingsScreen.name) {
                // Configure the navigation action
                popUpTo(MainNavOption.LoginScreen.name)
            }
        })
    }, modifier = Modifier
        //shows a shadow when clicked
        //            .clickable {
        //                focusManager.clearFocus()
        //            }
        //same result as clickable without the shadow
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }

    ) { innerPadding ->

        val isLandscape =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isLandscape) {
                Spacer(modifier = Modifier.weight(0.1f))
                LogoImage()
                Spacer(modifier = Modifier.weight(0.2f))
            }

            val radioOptions = listOf(baseUrlName, baseUrl2Name)
            val (selectedOption, onOptionSelected) = rememberSaveable {
                mutableStateOf(radioOptions[selectedUrl])
            }
            Column {
                radioOptions.forEach { text ->
                    Row(Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(text)
                            }
                        )
                        .padding(horizontal = 16.dp))
                    {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(text)
                            }
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge.merge(),
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))

            //State for the updated credentials
            val updatedUsername by rememberUpdatedState(username)
            val updatedPassword by rememberUpdatedState(password)

            if (!isNotSecuredApi) {

                //TextField with the CredentialManager
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .focusRequester(focusRequester),
                    label = { Text(stringResource(R.string.Username)) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle, contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(),
                    interactionSource = interactionSource
                )

                Spacer(modifier = Modifier.size(32.dp))

                // TextField for Password
                var isPasswordVisible by remember { mutableStateOf(false) }

                //TextField with the password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    label = { Text(stringResource(R.string.Password)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock, contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            isPasswordVisible = !isPasswordVisible
                        }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                contentDescription = "Password Visibility"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        onLogin(updatedUsername, updatedPassword)
                    }),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                )
            }

            Spacer(modifier = Modifier.weight(0.3f))

            val buttonModifier = if (isLandscape) {
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(400.dp)

            } else {
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            }

            //Button to login
            AppButton(modifier = buttonModifier, text = R.string.login, onClick = {
                onLogin(updatedUsername, updatedPassword)
            })

            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}

/**
 * Composable for the image of the login screen
 */
@Composable
fun LogoImage() {
    Image(
        painter = painterResource(R.drawable.mastroweb),
        contentDescription = "Logo Nipeservice",
        modifier = Modifier.size(150.dp)
    )
}


/**
 * Preview composable for the LoginScreen
 */
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val mutableInteractionSource = remember {
        MutableInteractionSource()
    }
    MastroAndroidTheme {
        Login(
            navController = NavController(LocalContext.current),
            interactionSource = mutableInteractionSource,
            onLogin = { _, _ -> },
            isSecondaryBaseUrlProvided = false,
            isNotSecuredApi = false,
            selectedUrl = 0,
            baseUrlName = "Primary",
            baseUrl2Name = "Secondary"
        )
    }
}
