package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mastrosql.app.R
import com.mastrosql.app.ui.components.appbar.DrawerIcon
import com.mastrosql.app.ui.theme.MastroAndroidPreviewTheme

/**
 * App bar to display title and conditionally display the back navigation.
 */
@ExperimentalMaterial3Api
@Composable
fun OrdersTopAppBar(
    modifier: Modifier = Modifier,
    drawerState: DrawerState? = null,
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
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
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.add_new_order),
                    modifier = Modifier.fillMaxSize(),
                    //tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

/**
 * Preview OrdersTopAppBar composable
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun OrdersTopAppBarPreview() {
    MastroAndroidPreviewTheme {
        OrdersTopAppBar(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            title = "Orders",
            onAddOrderClick = {}
        )
    }
}