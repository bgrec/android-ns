package com.mastrosql.app.ui.navigation.main.loginscreen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.components.OnClickFunction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAppBar(
    onClick: OnClickFunction,
    @StringRes title: Int? = null,
    subtitle : String? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Row {
                Text(
                    text = title?.let { stringResource(id = it) }
                        ?: stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleMedium
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onClick) {
                Icon(
                    Icons.Rounded.Settings,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = stringResource(id = R.string.drawer_settings_description)
                )
            }
        }
    )
}

@Preview
@Composable
fun LoginAppBarPreview() {
    LoginAppBar(onClick = {}, subtitle = "Subtitle")
}