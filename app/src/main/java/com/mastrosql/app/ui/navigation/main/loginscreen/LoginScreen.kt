package com.mastrosql.app.ui.navigation.main.loginscreen

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.components.AppButton
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.navigation.main.MainNavOption


@Composable
fun LoginScreen(
    drawerState: DrawerState,
    navController: NavController,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = { AppBar(drawerState = drawerState) }
    ) { it ->
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
                TextField(
                    value = username, // Set your initial value here or use a state variable to manage it
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                //Spacer(modifier = Modifier.height(30.dp)) // Add some space between the text fields
                Spacer(modifier = Modifier.weight(0.1f))
                // TextField for Password
                TextField(
                    value = password, // Set your initial value here or use a state variable to manage it
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            // Handle login button click here
                            navController.navigate(MainNavOption.HomeScreen.name)
                        }
                    ),

                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
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
                        navController.navigate(MainNavOption.HomeScreen.name)
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
fun LoginFields(modifier: Modifier) {

}

@Preview
@Composable
fun LoginScreenPreview() {
    Button(
        onClick = {},
    ) {
        Text("Login")
    }
}