package com.mastrosql.app.ui.components.appbar

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    drawerState: DrawerState? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    @StringRes title: Int? = null,
    appBarActions: List<AppBarAction>? = null,
    showDrawerIconButton: Boolean = true,
    backNavigationAction: (() -> Unit)? = null
) {

    CenterAlignedTopAppBar(
        // or TopAppBar
        title = {
            Text(
                text = title?.let { stringResource(id = it) }
                    ?: stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleMedium
            )
        },
        actions = {
            appBarActions?.let {
                for (appBarAction in it) {
                    AppBarAction(appBarAction)
                }
            }
        },
        navigationIcon = {
            if (backNavigationAction != null && backNavigationAction != {}) {
                // Check if back navigation action is provided and not an empty lambda
                BackNavigationIcon(backNavigationAction)
            } else if (drawerState != null && navigationIcon == null && showDrawerIconButton) {
                DrawerIcon(drawerState = drawerState)
            } else {
                navigationIcon?.invoke()
            }
        },
    )
}

@Composable
fun DrawerIcon(drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    IconButton(onClick = {
        coroutineScope.launch {
            drawerState.open()
        }
    }) {
        Icon(
            Icons.Rounded.Menu,
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = stringResource(id = R.string.drawer_menu_description)
        )
    }
}

@Composable
private fun BackNavigationIcon(backNavigationAction: () -> Unit) {
    IconButton(onClick = backNavigationAction) { // Execute the back navigation action
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = stringResource(id = R.string.back_button)
        )
    }
}

@Composable
fun AppBarAction(appBarAction: AppBarAction) {
    IconButton(onClick = appBarAction.onClick) {
        Icon(
            painter = painterResource(id = appBarAction.icon),
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = stringResource(id = appBarAction.description)
        )
    }
}