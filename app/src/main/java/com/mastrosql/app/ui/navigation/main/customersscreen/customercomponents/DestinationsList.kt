package com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData

@Composable
fun DestinationsList(
    modifier: Modifier = Modifier,
    destinationsDataList: List<DestinationData>,
    searchedTextState: MutableState<TextFieldValue>,
    onDestinationSelected: ((DestinationData?) -> Unit)? = null,
    //navController: NavController? = null
) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
        //.focusable()
    )
    {
        val filteredList: List<DestinationData>
        val searchedText = searchedTextState.value.text

        filteredList = if (searchedText.isEmpty()) {
            destinationsDataList
        } else {
            destinationsDataList.filter {
                it.destinationName?.contains(searchedText, ignoreCase = true) ?: true
            }
        }
        item {
            BlankDestinationCard(
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                //.focusable(),
                //navController = navController
            )
        }

        items(
            filteredList,
            key = {
                it.id
            })
        { destinationData ->
            DestinationCard(
                destinationData = destinationData,
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                //.focusable(),
                //navController = navController
            )
        }
    }
}
