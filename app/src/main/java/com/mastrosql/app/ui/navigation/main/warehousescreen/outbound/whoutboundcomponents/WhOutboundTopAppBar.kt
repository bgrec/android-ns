package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
@ExperimentalMaterial3Api
@Composable
fun WhOutboundTopAppBar(
    modifier: Modifier = Modifier,
    drawerState: DrawerState? = null,
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onAddWhOutboundClick: () -> Unit = {}
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
                onClick = { onAddWhOutboundClick() },
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = stringResource(id = R.string.add_wh_outbound),
                    modifier = Modifier.fillMaxSize(),
                    //tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
