package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mastrosql.app.R

/**
 * Top level composable that represents screens for the order details.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderDetailsComposable(navController: NavHostController = rememberNavController()) {
    OrderDetailsNavHost(navController = navController)
}

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
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
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
        }
    )
}
