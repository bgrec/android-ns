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
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@Composable
fun CustomersList(
    customerMasterDataList: List<CustomerMasterData>,
    searchedTextState: MutableState<TextFieldValue>,
    onCustomerSelected: ((CustomerMasterData) -> Unit)? = null,
    modifier: Modifier = Modifier,
    navController: NavController? = null
) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            //.focusable()
    )
    {
        val filteredList: List<CustomerMasterData>
        val searchedText = searchedTextState.value.text

        filteredList = if (searchedText.isEmpty()) {
            customerMasterDataList
        } else {
            customerMasterDataList.filter {
                it.businessName.contains(searchedText, ignoreCase = true)
            }
        }

        items(filteredList) { customerMasterData ->
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

@Preview
@Composable
fun ItemsListPreview() {
    MastroAndroidTheme {
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
                    emptyList(),
                    "taxId",
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
                    emptyList(),
                    "taxId",
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