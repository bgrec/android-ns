package com.mastrosql.app.ui.components.item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.ui.navigation.main.cartScreen.CartViewModel
import com.mastrosql.app.data.datasource.DataSourceTest
import com.mastrosql.app.data.itemTest.ItemTest

@Composable
fun ItemsList(
    itemsList: List<ItemTest>,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: CartViewModel = viewModel()
) {
    LazyColumn(modifier = modifier) {
        items(itemsList) { item ->
            val quantityState = rememberSaveable { mutableStateOf(0.00) }
            ItemCard(
                itemTest = item,
                orderQuantity = quantityState.value,
                onQuantityChange = { newQuantity ->
                    quantityState.value = newQuantity
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
        itemsList = DataSourceTest().loadItems(),
        modifier = Modifier.padding(8.dp),
        navController = NavController(LocalContext.current)
    )
}