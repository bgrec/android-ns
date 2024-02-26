package com.mastrosql.app.ui.navigation.main.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.navigation.intro.IntroViewModel
import com.mastrosql.app.ui.navigation.main.AppNavigationViewModel
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.NavRoutes
import com.mastrosql.app.ui.navigation.main.loginscreen.LogoImage

@Composable
fun NewHomeScreen(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navController: NavController,
    viewModel: IntroViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val gestureViewModel: AppNavigationViewModel = AppViewModelProvider.LocalAppNavigationViewModelProvider.current

    Scaffold(
        topBar = {
            AppBar(
                drawerState = drawerState,
                title = R.string.drawer_new_home,
                showDrawerIconButton = false
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
                    navController.navigate(MainNavOption.CustomersScreen.name) {
                        popUpTo(MainNavOption.NewHomeScreen.name)
                        //gestureViewModel.setGesturesEnabled(true)
                        gestureViewModel.setCurrentScreen(MainNavOption.CustomersScreen)
                    }
                    /*navController.currentDestination?.route?.let { route ->
                        gestureViewModel.setGesturesEnabled(route == NavRoutes.MainRoute.name)
                    }*/
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
                    navController.navigate(MainNavOption.CustomersPagedScreen.name) {
                        popUpTo(MainNavOption.NewHomeScreen.name)
                        //gestureViewModel.setGesturesEnabled(true)
                        gestureViewModel.setCurrentScreen(MainNavOption.CustomersScreen)
                        //popUpTo(NavRoutes.MainRoute.name)
                        //gestureViewModel.setGesturesEnabled(true)

                    }
                }
            ) {
                Text(text = stringResource(R.string.drawer_customers2))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    navController.navigate(MainNavOption.ArticlesScreen.name){
                        popUpTo(NavRoutes.MainRoute.name)
                        gestureViewModel.setGesturesEnabled(true)
                    }
                }
            ) {
                Text(text = stringResource(R.string.drawer_articles))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    navController.navigate(MainNavOption.ItemsScreen.name){
                        popUpTo(NavRoutes.MainRoute.name)
                        gestureViewModel.setGesturesEnabled(true)
                    }
                }
            ) {
                Text(text = stringResource(R.string.drawer_inventory))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    navController.navigate(MainNavOption.OrdersScreen.name){
                        popUpTo(NavRoutes.MainRoute.name)
                        gestureViewModel.setGesturesEnabled(true)
                    }
                }
            ) {
                Text(text = stringResource(R.string.drawer_orders))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    navController.navigate(MainNavOption.SettingsScreen.name){
                        popUpTo(NavRoutes.MainRoute.name)
                        gestureViewModel.setGesturesEnabled(true)
                    }
                }
            ) {
                Text(text = stringResource(R.string.drawer_settings))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    navController.navigate(MainNavOption.CartScreen.name){
                        popUpTo(NavRoutes.MainRoute.name)
                        gestureViewModel.setGesturesEnabled(true)
                    }
                }
            ) {
                Text(text = stringResource(R.string.drawer_cart))
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    //logout
                    navController.navigate(MainNavOption.LoginScreen.name){
                        popUpTo(NavRoutes.MainRoute.name)
                        gestureViewModel.setGesturesEnabled(false)
                    }
                    //TO-DO logout

                }
            ) {
                Text(text = stringResource(R.string.drawer_logout_description))
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