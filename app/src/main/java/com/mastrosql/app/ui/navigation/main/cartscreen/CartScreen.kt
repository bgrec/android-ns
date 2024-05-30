package com.mastrosql.app.ui.navigation.main.cartscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.ui.theme.MastroAndroidTheme


data class Articolo(val nome: String)

@Composable
fun ArrowBackButton(navController: NavController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun CartScreen(
    drawerState: DrawerState,
    navController: NavController
) {

    val viewModel = CartViewModel()
    val cartItems by viewModel.cartItems.collectAsState()

    var expandedListIndex by remember { mutableIntStateOf(-1) }
    // val cartViewModel = viewModel<CartViewModel>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ShoppingCart") },
                navigationIcon = {
                    ArrowBackButton(navController = navController)
                },
                //  backgroundColor = Color.Gray // Set the background color here
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in cartItems.indices) {
                val isExpanded = expandedListIndex == i

                OutlinedButton(
                    onClick = {
                        expandedListIndex = if (isExpanded) -1 else i
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (cartItem in cartItems) {
                            // ... (display cart item details)
                            cartItem.ean?.let { it1 -> Text(it1) }
                        }
                    }
                }

                if (isExpanded) {
                    Column {
                        for (cartItem in cartItems) {
                            // ... (display cart item details)
                            cartItem.description?.let { it1 -> Text(it1) }

                        }
                    }
                }
            }
        }
    }
}

/**
 * Preview for [CartScreen]
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MastroAndroidTheme {
        CartScreen(
            drawerState = rememberDrawerState(DrawerValue.Closed),
            navController = NavController(LocalContext.current)
        )
    }
}
