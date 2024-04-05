package com.mastrosql.app.ui.navigation.main.errorScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.R.string.retry
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.settingsscreen.UserPreferencesViewModel
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import retrofit2.HttpException

/**
 * The home screen displaying error message with re-attempt button.
 */

@Composable
fun ErrorScreen(
    exception: Exception,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    drawerState: DrawerState? = null,
    navController: NavController,
    preferencesViewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Text(text = exception.message.toString(), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(retry))
        }
        if (exception is HttpException && exception.code() == 401) {
            preferencesViewModel.logout(navController)
        }
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    val modifier = Modifier
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = NavController(LocalContext.current)
    //val viewModel = HiltViewModelFactory(LocalContext.current, navController).create(UserPreferencesViewModel::class.java

    MastroAndroidTheme {
        ErrorScreen(
            exception = Exception("errore"),
            retryAction = {},
            modifier = modifier,
            drawerState = drawerState,
            navController = navController
        )
    }
}
