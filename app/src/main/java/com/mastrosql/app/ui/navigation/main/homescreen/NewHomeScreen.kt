package com.mastrosql.app.ui.navigation.main.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@Composable
fun NewHomeScreen(
    drawerState: DrawerState,
    navController: NavController,
) {
    Scaffold(
        topBar = {
            AppBar(
                drawerState = drawerState,
                title = R.string.drawer_new_home
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button (
                modifier = Modifier.padding(4.dp),
                onClick = {

                },
            ){
                Text(text = "clienti")
            }
        }
    }
}


@Preview(apiLevel = 33)
@Composable
fun NewHomeScreenPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        NewHomeScreen(drawerState, NavController(LocalContext.current))
}