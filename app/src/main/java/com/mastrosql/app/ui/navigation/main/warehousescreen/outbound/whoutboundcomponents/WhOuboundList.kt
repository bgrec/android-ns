package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents

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
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound

@Composable
fun WhOutboundList(
    modifier: Modifier,
    listState: LazyListState,
    whOutboundList: List<WarehouseOutbound>,
    modifiedOrderId: MutableIntState?,
    searchTextState: MutableState<TextFieldValue>,
    navigateToWhOutboundDetails: (Int, String?) -> Unit,
    showEditWhOutboundDataDialog: MutableState<Boolean>
) {
    LaunchedEffect(whOutboundList) {
        modifiedOrderId?.intValue.let { modifiedOrderId ->
            if (modifiedOrderId != null && modifiedOrderId > 0) {
                val index = whOutboundList.indexOfFirst { it.id == modifiedOrderId }
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

        val filteredList = filterWhOutbounds(whOutboundList, searchTextState.value.text)

        items(filteredList, key = {
            it.id
        }) { whOutbound ->

            WhOutboundCard(
                whOutbound = whOutbound,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                //.focusable(),
                //navController = navController,
                navigateToOrderDetails = navigateToWhOutboundDetails,
                modifiedOrderId = modifiedOrderId,
                showEditOrderDataDialog = showEditWhOutboundDataDialog
            )
        }

        item { Spacer(modifier = Modifier.padding(70.dp)) }
    }
}

private fun filterWhOutbounds(
    ordersList: List<WarehouseOutbound>,
    searchedText: String
): List<WarehouseOutbound> {
    return if (searchedText.isEmpty()) ordersList
    else ordersList.filter {
        it.businessName?.contains(
            searchedText, ignoreCase = true
        ) ?: true || it.city?.contains(searchedText, ignoreCase = true) ?: true
    }
}
