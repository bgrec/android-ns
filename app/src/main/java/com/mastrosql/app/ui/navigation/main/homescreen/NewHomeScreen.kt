package com.mastrosql.app.ui.navigation.main.homescreen

import android.content.pm.PackageManager.ComponentEnabledSetting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.components.AppButton
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersScreen
import com.mastrosql.app.ui.navigation.main.loginscreen.LogoImage

@Composable
fun NewHomeScreen(
    drawerState: DrawerState,
    navController: NavController
) {
    Scaffold(
        topBar = {
            AppBar(
                drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
                title = R.string.drawer_new_home
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center
                )
                {
                    LogoImage()
                }
            }

            Spacer(Modifier.height(30.dp))

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {

                }
            ) {
                Text(
                    text = stringResource(R.string.drawer_customers),
                    textAlign = TextAlign.Center
                )
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {

                }
            ) {
                Text(text = stringResource(R.string.drawer_customers2))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {

                }
            ) {
                Text(text = stringResource(R.string.drawer_articles))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {

                }
            ) {
                Text(text = stringResource(R.string.drawer_inventory))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {

                }
            ) {
                Text(text = stringResource(R.string.drawer_orders))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {

                }
            ) {
                Text(text = stringResource(R.string.drawer_settings))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {

                }
            ) {
                Text(text = stringResource(R.string.drawer_cart))
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