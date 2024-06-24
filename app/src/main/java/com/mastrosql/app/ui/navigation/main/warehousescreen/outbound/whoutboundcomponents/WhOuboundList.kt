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
    modifiedWhOutboundId: MutableIntState?,
    searchTextState: MutableState<TextFieldValue>,
    navigateToWhOutboundDetails: (Int, String?) -> Unit,
    showEditWhOutboundDataDialog: MutableState<Boolean>
) {
    LaunchedEffect(whOutboundList) {
        modifiedWhOutboundId?.intValue.let { modifiedWhOutboundId ->
            if (modifiedWhOutboundId != null && modifiedWhOutboundId > 0) {
                val index = whOutboundList.indexOfFirst { it.id == modifiedWhOutboundId }
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
                navigateToWhOutboundDetails = navigateToWhOutboundDetails,
                modifiedWhOutboundId = modifiedWhOutboundId,
                showEditWhOutboundDataDialog = showEditWhOutboundDataDialog
            )
        }
        item { Spacer(modifier = Modifier.padding(32.dp)) }
    }
}

private fun filterWhOutbounds(
    whOutboundList: List<WarehouseOutbound>,
    searchedText: String
): List<WarehouseOutbound> {
    return if (searchedText.isEmpty()) whOutboundList
    else whOutboundList.filter {
        it.businessName?.contains(
            searchedText, ignoreCase = true
        ) ?: true || it.city?.contains(searchedText, ignoreCase = true) ?: true
    }
}
