package com.mastrosql.app.ui.navigation.main.loadingscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R

/**
 * The home screen displaying the loading message.
 */

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    navController: NavController
) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}


@Preview
@Composable
fun LoadingScreenPreview() {
    val modifier = Modifier
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = NavController(LocalContext.current)

    LoadingScreen(modifier, drawerState, navController)
}
