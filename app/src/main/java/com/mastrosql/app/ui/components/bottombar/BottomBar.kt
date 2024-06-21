package com.mastrosql.app.ui.components.bottombar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.ui.navigation.main.MainNavOption


@Composable
fun BottomBar(
    //drawerState: DrawerState? = null,
    navController: NavController,
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.navigate(MainNavOption.CartScreen.name)
            }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
            }
            IconButton(onClick = { /* Gestisci l'azione dei preferiti */ }) {
                Icon(Icons.Default.Star, contentDescription = null)
            }
            IconButton(onClick = { /* Gestisci l'azione del carrello */ }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }
}


@Composable
fun BottomBarAction(bottomBarAction: BottomBarAction) {
    IconButton(onClick = bottomBarAction.onClick) {
        Icon(
            painter = painterResource(id = bottomBarAction.icon),
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = stringResource(id = bottomBarAction.description)
        )
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    val scope = rememberCoroutineScope()
    BottomBar(
        //drawerState = null,
        navController = NavController(LocalContext.current),
    )
}