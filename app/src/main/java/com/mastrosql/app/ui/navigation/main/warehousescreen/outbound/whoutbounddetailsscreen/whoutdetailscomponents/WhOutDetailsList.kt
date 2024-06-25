package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.whoutdetailscomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mastrosql.app.data.local.SwipeActionsPreferences
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model.WhOutDetailsItem

/**
 * WhOutDetailsList composable to display the list of whOut details
 */
@Composable
fun WhOutDetailList(
    modifier: Modifier = Modifier,
    whOutDetailList: List<WhOutDetailsItem>,
    searchTextState: MutableState<TextFieldValue>,
    showEditDialog: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    modifiedIndex: MutableIntState?,
    onRemove: (Int) -> Unit,
    onDuplicate: (Int) -> Unit,
    swipeActionsPreferences: SwipeActionsPreferences
) {
    // MutableState to store the modified item id
    val modifiedItemId = remember { mutableIntStateOf(0) }

    // LazyListState to scroll to the modified item
    val listState = rememberLazyListState()

    // Scroll to the modified item when the list changes
    LaunchedEffect(whOutDetailList) {
        modifiedIndex?.let { index ->
            if (index.intValue >= 0) {
                listState.animateScrollToItem(index.intValue)
                modifiedItemId.intValue = whOutDetailList[index.intValue].id
            }
        }
    }

    //Set the modifiedIndex to the index of the modified item when clicked on edit
    LaunchedEffect(modifiedItemId.intValue) {
        if (modifiedItemId.intValue > 0) {
            modifiedIndex?.intValue =
                whOutDetailList.indexOfFirst { it.id == modifiedItemId.intValue }
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

        //Filter the list based on the search text
        val filteredList =
            filterWhOutDetailList(whOutDetailList, searchTextState.value.text)

        items(
            filteredList,
            key = {
                it.id
            })
        { whOutDetail ->

            WhOutDetailsCard(
                whOutDetailsItem = whOutDetail,
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                //.focusable(),
                onRemove = onRemove,
                showEditDialog = showEditDialog,
                snackbarHostState = snackbarHostState,
                listState = listState,
                modifiedItemId = modifiedItemId,
                onDuplicate = onDuplicate,
                swipeActionsPreferences = swipeActionsPreferences
            )
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

// Filter the list based on the search text
private fun filterWhOutDetailList(
    whOutDetailList: List<WhOutDetailsItem>,
    searchText: String
): List<WhOutDetailsItem> {
    return if (searchText.isEmpty()) {
        whOutDetailList
    } else {
        whOutDetailList.asSequence()
            .filter { item ->
                item.description?.contains(searchText, ignoreCase = true) == true ||
                        item.articleId.toString().contains(searchText, ignoreCase = true) ||
                        item.sku?.contains(searchText, ignoreCase = true) == true
            }
            .toList() // Convert sequence back to list
    }
}
