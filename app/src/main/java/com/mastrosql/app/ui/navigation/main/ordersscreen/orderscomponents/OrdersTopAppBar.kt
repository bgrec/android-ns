package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mastrosql.app.R
import com.mastrosql.app.ui.components.appbar.DrawerIcon

/**
 * App bar to display title and conditionally display the back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersTopAppBar(
    modifier: Modifier = Modifier,
    drawerState: DrawerState? = null,
    title: String,
    canNavigateBack: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    onAddOrderClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (drawerState != null) {
                DrawerIcon(drawerState = drawerState)
            }
        },
        actions = {
            IconButton(
                onClick = { onAddOrderClick() },
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.add_new_order),
                    modifier = Modifier.fillMaxSize(),
                    //tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}