package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.ui.tooling.preview.Preview
import com.mastrosql.app.R
import com.mastrosql.app.ui.theme.MastroAndroidPreviewTheme

/**
 * App bar to display title and conditionally display the back navigation.
 */

// TODO: Add the WhOutDetailsTopAppBar composable here from file
@ExperimentalMaterial3Api
@Composable
fun WhOutDetailsTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    canNavigateBack: Boolean,
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
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = stringResource(R.string.row_details_add_button)
                )
            }
        }
    )
}

/**
 * Preview for [WhOutDetailsTopAppBar]
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun WhOutDetailsTopAppBarPreview() {
    MastroAndroidPreviewTheme {
        WhOutDetailsTopAppBar(
            title = "WhOut Details",
            canNavigateBack = true,
            onAddItemClick = {}
        )
    }
}