package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.orderscomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

/**
 * A list of [Order]s
 */
@Composable
fun OrdersList(
    modifier: Modifier,
    listState: LazyListState,
    ordersList: List<com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order>,
    modifiedOrderId: MutableIntState?,
    searchTextState: MutableState<TextFieldValue>,
    navigateToOrderDetails: (Int, String?) -> Unit,
    showEditDeliveryDialog: MutableState<Boolean>,
    showEditOrderDataDialog: MutableState<Boolean>
) {
    LaunchedEffect(ordersList) {
        modifiedOrderId?.intValue.let { modifiedOrderId ->
            if (modifiedOrderId != null && modifiedOrderId > 0) {
                val index = ordersList.indexOfFirst { it.id == modifiedOrderId }
                //Log.d("OrdersList", "Scrolling to index $index")
                listState.animateScrollToItem(index)
            }
        }
    }

    LazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        //pass the listState to the LazyColumn to be able to scroll to the modified item
        state = listState,
        //.focusable()
    ) {

        val filteredList = filterOrders(ordersList, searchTextState.value.text)

        items(filteredList, key = {
            it.id
        }) { order ->

            WhOutboundCard(
                order = order,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                //.focusable(),
                //navController = navController,
                navigateToOrderDetails = navigateToOrderDetails,
                modifiedOrderId = modifiedOrderId,
                showEditDeliveryDialog = showEditDeliveryDialog,
                showEditOrderDataDialog = showEditOrderDataDialog
            )
        }

        item { Spacer(modifier = Modifier.padding(70.dp)) }
    }
}

private fun filterOrders(
    ordersList: List<com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order>,
    searchedText: String
): List<com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order> {
    return if (searchedText.isEmpty()) ordersList
    else ordersList.filter {
        it.description?.contains(
            searchedText, ignoreCase = true
        ) ?: true || it.businessName?.contains(
            searchedText, ignoreCase = true
        ) ?: true || it.city?.contains(searchedText, ignoreCase = true) ?: true
    }
}
