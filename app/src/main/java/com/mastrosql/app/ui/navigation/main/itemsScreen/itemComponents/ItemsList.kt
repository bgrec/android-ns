package com.mastrosql.app.ui.navigation.main.itemsScreen.itemComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.data.datasource.DataSourceTest_cancel_it_Later
import com.mastrosql.app.data.itemTest.ItemTest
import com.mastrosql.app.ui.navigation.main.cartscreen.CartViewModel

@Composable
fun ItemsList(
    itemsList: List<ItemTest>,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: CartViewModel = viewModel()
) {
    LazyColumn(modifier = modifier) {
        items(itemsList) { item ->
            val quantityState = rememberSaveable { mutableDoubleStateOf(0.00) }
            ItemCard(
                itemTest = item,
                orderQuantity = quantityState.doubleValue,
                onQuantityChange = { newQuantity ->
                    quantityState.doubleValue = newQuantity
                },

                modifier =
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                navController = navController,
                viewModel = viewModel

            )
        }
    }
}

@Preview
@Composable
fun ItemsListPreview() {
    ItemsList(
        itemsList = DataSourceTest_cancel_it_Later().loadItems(),
        modifier = Modifier.padding(8.dp),
        navController = NavController(LocalContext.current)
    )
}