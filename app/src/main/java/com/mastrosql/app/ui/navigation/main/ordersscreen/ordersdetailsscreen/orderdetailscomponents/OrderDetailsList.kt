package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderDetailList(
    orderDetailList: List<OrderDetailsItem>,
    state: MutableState<TextFieldValue>,
    modifier: Modifier = Modifier,
    navController: NavController,
    showEditDialog: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    modifiedIndex: Int?,
    onRemove: (Int) -> Unit,
    onDuplicate: (Int) -> Unit
) {

    val listState = rememberLazyListState()
    // Scroll to the modified item when the list changes
    LaunchedEffect(orderDetailList) {
        //Log.d("OrderDetailList", "modifiedIndex: $modifiedIndex")
        modifiedIndex?.let { index ->
            listState.animateScrollToItem(index)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .fillMaxHeight()
    )
    {
        val filteredList: List<OrderDetailsItem>
        val searchedText = state.value.text

        filteredList = if (searchedText.isEmpty()) {
            orderDetailList
        } else {
            //update this for fields to search
            orderDetailList.filter {
                it.description?.contains(searchedText, ignoreCase = true) == true
            }
        }

        items(
            filteredList,
            key = {
                it.id
            })
        { orderDetail ->
            OrderDetailsItem(
                orderDetailsItem = orderDetail,
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                //.focusable(),
                navController = navController,
                navigateToEditItem = {},
                onRemove = onRemove,
                showEditDialog = showEditDialog,
                snackbarHostState = snackbarHostState,
                listState = listState,
                modifiedItemId = remember { mutableStateOf(if (orderDetailList.indexOf(orderDetail) == modifiedIndex) orderDetail.id else null) },
                onDuplicate = onDuplicate
            )
        }

        item {
            Spacer(modifier = Modifier.height(17.dp))
        }
    }
}


/*
@Preview
@Composable
fun ItemsListPreview() {
    MastroAndroidTheme {
        OrderDetailList(
            orderDetailList = listOf(
                OrderDetailsItem(
                    id = 1,
                    orderId = 1,
                    orderRow = 1,
                    confirmed = true,
                    articleId = 1,
                    sku = "sku",
                    vendorSku = "vendorSku",
                    description = "description",
                    completeDescription = "completeDescription",
                    quantity = 1.0,
                    tmpQuantity = 1.0,
                    returnedQuantity = 1.0,
                    weight = 1.0,
                    cost = "1.0",
                    price = "1.0",
                    vat = "vat",
                    vatValue = 1.0,
                    discount = 1.0,
                    discount1 = 1.0,
                    discount2 = 1.0,
                    discount3 = 1.0,
                    catalogPrice = 1.0,
                    measureUnit = "measureUnit",
                    rowType = 1,
                    packSize = "packSize",
                    orderedQuantity = 1.0,
                    shippedQuantity = 1.0,
                    batch = "batch",
                    expirationDate = "2023-01-01",
                    links = listOf(),

                    metadata = Metadata(
                        etag = "etag"
                    ),
                    page = 0,
                    lastUpdated = System.currentTimeMillis()
                ),
                OrderDetailsItem(
                    id = 1,
                    orderId = 1,
                    orderRow = 1,
                    confirmed = true,
                    articleId = 1,
                    sku = "sku",
                    vendorSku = "vendorSku",
                    description = "description",
                    completeDescription = "completeDescription",
                    quantity = 1.0,
                    tmpQuantity = 1.0,
                    returnedQuantity = 1.0,
                    weight = 1.0,
                    cost = 1.0,
                    price = 1.0,
                    vat = "vat",
                    vatValue = 1.0,
                    discount = 1.0,
                    discount1 = 1.0,
                    discount2 = 1.0,
                    discount3 = 1.0,
                    catalogPrice = 1.0,
                    measureUnit = "measureUnit",
                    rowType = 1,
                    packSize = "packSize",
                    orderedQuantity = 1.0,
                    shippedQuantity = 1.0,
                    batch = "batch",
                    expirationDate = "2023-01-01",
                    links = listOf(),

                    metadata = com.mastrosql.app.ui.navigation.main.ordersdetailsscreen.model.Metadata(
                        etag = "etag"
                    ),
                    page = 0,
                    lastUpdated = System.currentTimeMillis()
                )
            ),
            state = remember { mutableStateOf(TextFieldValue("")) },
            modifier = Modifier.padding(8.dp),
            navController = NavController(LocalContext.current)
        )
    }
}
*/
