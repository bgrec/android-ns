package com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.Metadata
import com.mastrosql.app.ui.theme.MastroAndroidPreviewTheme

@Composable
fun CustomersList(
    modifier: Modifier = Modifier,
    customerMasterDataList: List<CustomerMasterData>,
    searchedTextState: MutableState<TextFieldValue>,
    onCustomerSelected: ((CustomerMasterData) -> Unit)? = null,
    navController: NavController? = null
) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
        //.focusable()
    )
    {
        val filteredList = filterCustomersList(customerMasterDataList, searchedTextState.value.text)

        items(filteredList,
            key = {
                it.id
            }) { customerMasterData ->
            CustomerCard(
                customerMasterData = customerMasterData,
                onCustomerSelected = onCustomerSelected,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                //.focusable(),
                navController = navController
            )
        }
    }
}

/*
* val listState = rememberLazyListState()

LazyColumn(state = listState) {
    // ...
}

val showButton by remember {
    derivedStateOf {
        listState.firstVisibleItemIndex > 0
    }
}

AnimatedVisibility(visible = showButton) {
    ScrollToTopButton()
}*/

// Function to filter the list based on the search text
private fun filterCustomersList(
    customersList: List<CustomerMasterData>,
    searchText: String
): List<CustomerMasterData> {
    return if (searchText.isEmpty()) {
        customersList
    } else {
        customersList.filter { item ->
            item.businessName?.contains(searchText, ignoreCase = true) == true
        }
//        customersList.asSequence()
//            .filter { item ->
//                item.businessName?.contains(searchText, ignoreCase = true) == true
//            }
//            .toList() // Convert sequence back to list
    }
}

@Preview
@Composable
fun CustomersListPreview() {
    MastroAndroidPreviewTheme {
        CustomersList(
            customerMasterDataList = listOf(
                CustomerMasterData(
                    1, "businessName1",
                    "street",
                    "postalCode",
                    "vat",
                    "city",
                    "province",
                    "nation",
                    "businessName2",
                    "taxId",
                    emptyList(),
                    Metadata("etag"),
                    0,
                    0L
                ),
                CustomerMasterData(
                    2, "businessName2",
                    "street",
                    "postalCode",
                    "vat",
                    "city",
                    "province",
                    "nation",
                    "businessName2",
                    "taxId",
                    emptyList(),
                    Metadata("etag"),
                    0,
                    0L
                )
            ),
            searchedTextState = remember { mutableStateOf(TextFieldValue("")) },
            modifier = Modifier.padding(8.dp),
            navController = NavController(LocalContext.current)
        )
    }
}