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
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.R.string.retry
import com.mastrosql.app.ui.theme.MastroAndroidTheme

/**
 * The home screen displaying error message with re-attempt button.
 */

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    navController: NavController
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(retry))
        }
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    val modifier = Modifier
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = NavController(LocalContext.current)

    MastroAndroidTheme {
        ErrorScreen(retryAction = {},modifier, drawerState, navController)
    }
}
