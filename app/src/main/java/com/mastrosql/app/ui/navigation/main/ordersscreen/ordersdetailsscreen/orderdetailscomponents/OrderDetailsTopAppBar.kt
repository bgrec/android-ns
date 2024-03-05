package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R

/**
 * App bar to display title and conditionally display the back navigation.
 */

// TODO: Add the OrderDetailsTopAppBar composable here from file
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    onAddItemClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            if (title.length > 20) {
                // If the title is longer than 20 characters, reduce the font size
                Text(
                    text = title,
                    fontSize = typography.titleMedium.fontSize,
                )
            } else {
                // Default font size
                Text(
                    text = title,
                    fontSize = typography.titleLarge.fontSize, // Default font size
                )
            }
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { onAddItemClick() },
                //modifier = Modifier.padding(end = 16.dp) // Add padding to the button
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.order_details_add_button)
                )
            }
        }
    )
}