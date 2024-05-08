package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Metadata
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.theme.MastroAndroidTheme


@Composable
fun OrdersList(
    modifier: Modifier,
    ordersList: List<Order>,
    modifiedOrderId: MutableIntState?,
    searchTextState: MutableState<TextFieldValue>,
    navController: NavController,
    navigateToOrderDetails: (Int, String?) -> Unit,
    showDeliveryDialog: MutableState<Boolean>
) {
    val listState = rememberLazyListState()
    // Scroll to the modified item when the list changes
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
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
        //pass the listState to the LazyColumn to be able to scroll to the modified item
        state = listState,
        //.focusable()
    )
    {

        val filteredList = filterOrders(ordersList, searchTextState.value.text)

        items(
            filteredList,
            key = {
                it.id
            })
        { order ->

            OrderCard(
                order = order,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                //.focusable(),
                //navController = navController,
                navigateToOrderDetails = navigateToOrderDetails,
                modifiedOrderId = modifiedOrderId,
                showDeliveryDialog = showDeliveryDialog
            )
        }

        item { Spacer(modifier = Modifier.padding(70.dp)) }
    }
}

private fun filterOrders(ordersList: List<Order>, searchedText: String): List<Order> {
    return if (searchedText.isEmpty())
        ordersList
    else
        ordersList.filter {
            it.description?.contains(searchedText, ignoreCase = true) ?: true
                    ||
                    it.businessName?.contains(searchedText, ignoreCase = true) ?: true
                    || it.city?.contains(searchedText, ignoreCase = true) ?: true
        }
}

@Preview
@Composable
fun OrdersListPreview() {
    MastroAndroidTheme {
        OrdersList(
            ordersList = listOf(
                Order(

                    id = 2,
                    clientId = 1,
                    businessName = "businessName",
                    street = "street",
                    postalCode = "postalCode",
                    city = "city",
                    province = "province",
                    nation = "nation",
                    destinationId = 1,
                    destinationName = "destinationName",
                    description = "description",
                    sequence = 1,
                    insertDate = "2023-01-01",
                    agent = "agent",
                    transportHandler = "transportHandler",
                    parcels = 1,
                    carrierId = 1,
                    carrierName = "carrierName",
                    weight = 1.0,
                    port = "port",
                    date = "2023-01-01",
                    notes = "notes",
                    deliveryDate = "2023-01-01",
                    deliveryDeadline = true,
                    deliveryType = 1,
                    deliveryState = 1,
                    urgent = true,
                    partial = 1,
                    number = 1,

                    links = emptyList(),
                    metadata = Metadata("etag"),
                    page = 0,
                    lastUpdated = System.currentTimeMillis()
                ),
                Order(
                    id = 1,
                    clientId = 1,
                    businessName = "businessName",
                    street = "street",
                    postalCode = "postalCode",
                    city = "city",
                    province = "province",
                    nation = "nation",
                    destinationId = 1,
                    destinationName = "destinationName",
                    description = "description",
                    sequence = 1,
                    insertDate = "2023-01-01",
                    agent = "agent",
                    transportHandler = "transportHandler",
                    parcels = 1,
                    carrierId = 1,
                    carrierName = "carrierName",
                    weight = 1.0,
                    port = "port",
                    date = "2023-01-01",
                    notes = "notes",
                    deliveryDate = "2023-01-01",
                    deliveryDeadline = true,
                    deliveryType = 1,
                    deliveryState = 1,
                    urgent = true,
                    partial = 1,
                    number = 1,

                    links = emptyList(),
                    metadata = Metadata("etag"),
                    page = 0,
                    lastUpdated = System.currentTimeMillis()
                )
            ),
            searchTextState = remember { mutableStateOf(TextFieldValue("")) },
            modifier = Modifier.padding(8.dp),
            navController = NavController(LocalContext.current),
            modifiedOrderId = remember { mutableIntStateOf(0) },
            navigateToOrderDetails = { _, _ -> },
            showDeliveryDialog = remember { mutableStateOf(false) }
        )
    }
}