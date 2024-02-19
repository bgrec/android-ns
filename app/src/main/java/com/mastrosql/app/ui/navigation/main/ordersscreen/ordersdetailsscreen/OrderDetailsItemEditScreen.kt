package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.itemsScreen.NavigationDestination
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import kotlinx.coroutines.launch

object OrderDetailsItemEditDestination : NavigationDestination {
    override val route = "order_details_item_edit"
    override val titleRes = R.string.edit_order_details_title
    const val rowIdArg = "rowId"
    val routeWithArgs = "$route/{$rowIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsItemEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderDetailsItemEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            OrderDetailsTopAppBar(
                title = stringResource(OrderDetailsItemEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        OrderDetailsEntryBody(
            ordersDetailsUiState = viewModel.orderDetailsUiState,
            onItemValueChange = {},//viewModel::updateUiState,
            onSaveClick = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be updated in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    //viewModel.updateItem()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
//TODO check the use of Divider() composable
@Preview(showBackground = true)
@Composable
fun OrderDetailsEditScreenPreview() {
    MastroAndroidTheme {
        OrderDetailsItemEditScreen(navigateBack = { /*Do nothing*/ }, onNavigateUp = { /*Do nothing*/ })
    }
}
/*
Text(
                    text = SimpleDateFormat("h:mm a", Locale.getDefault())
                        .format(Date(schedule.arrivalTimeInMillis.toLong() * 1000)),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = dimensionResource(R.dimen.font_large).value.sp,
                        fontWeight = FontWeight(600)
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(2f)
                )
 */