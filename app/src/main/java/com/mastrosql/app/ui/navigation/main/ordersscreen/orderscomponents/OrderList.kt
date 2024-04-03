package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrdersList(
    modifier: Modifier,
    ordersList: List<Order>,
    modifiedOrderId: MutableState<Int>,
    state: MutableState<TextFieldValue>,
    navController: NavController,
    navigateToOrderDetails: (Int, String?) -> Unit,
    showDeliveryDialog: MutableState<Boolean>
) {
    val listState = rememberLazyListState()
    // Scroll to the modified item when the list changes
    LaunchedEffect(ordersList) {
        //Log.d("OrderDetailList", "modifiedIndex: $modifiedIndex")
        modifiedOrderId.value.let { index ->
            listState.animateScrollToItem(index)
        }
    }

    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
        //.focusable()
    )
    {
        val filteredList: List<Order>
        val searchedText = state.value.text

        filteredList = if (searchedText.isEmpty()) {
            ordersList
        } else {
            //update this for fields to search
            ordersList.filter {
                it.description?.contains(searchedText, ignoreCase = true) ?: true
                        ||
                        it.businessName?.contains(searchedText, ignoreCase = true) ?: true
                        || it.city?.contains(searchedText, ignoreCase = true) ?: true

            }
        }

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
                navController = navController,
                navigateToOrderDetails = navigateToOrderDetails,
                modifiedOrderId = modifiedOrderId,
                showDeliveryDialog = showDeliveryDialog
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
            state = remember { mutableStateOf(TextFieldValue("")) },
            modifier = Modifier.padding(8.dp),
            navController = NavController(LocalContext.current),
            modifiedOrderId = remember { mutableIntStateOf(0) },
            navigateToOrderDetails = { _, _ -> },
            showDeliveryDialog = remember { mutableStateOf(false) }
        )
    }
}