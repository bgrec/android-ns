package com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun CustomersPagedList(
    customers: LazyPagingItems<CustomerMasterData>,
    modifier: Modifier = Modifier,
    navController: NavController,
    drawerState: DrawerState
) {
    val refreshScope = rememberCoroutineScope()
    val refreshing by remember { mutableStateOf(false) }

    val loadState: CombinedLoadStates = customers.loadState

    when (loadState.refresh) {
        is LoadState.Loading -> {
            // Display a loading indicator while initial data is being fetched.
            //CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            LoadingScreen(
                modifier = modifier.fillMaxSize(),
                loading = true
            )
        }

        is LoadState.Error -> {
            // Handle error state (e.g., display an error message).
            //Text(text = "Error: ${loadState.refresh}")
            ErrorScreen(
                (loadState.refresh as LoadState.Error).error as Exception,
                { customers.refresh() },
                modifier = modifier.fillMaxSize(),
                navController = navController
            )
        }

        else -> {
            //val state = rememberPullRefreshState(refreshing, ::refresh)
            val pullRefreshState = rememberPullRefreshState(
                refreshing,
                { refreshScope.launch { customers.refresh() } })

            Box(
                Modifier
                    .pullRefresh(pullRefreshState)
                //.verticalScroll(rememberScrollState())
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                    //.padding(8.dp)
                )
                {
                    if (!refreshing) {
                        items(
                            count = customers.itemCount,
                            key = customers.itemKey { it.id },
                            contentType = customers.itemContentType { "contentType" }) { index ->
                            customers[index]?.let {
                                CustomerCard(
                                    customerMasterData = it,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    //.focusable(),
                                    navController = navController
                                )
                            }
                        }
                    }

                    val loadState = customers.loadState.mediator

                    item {
                        if (loadState?.refresh == LoadState.Loading) {
                            Column(
                                modifier = Modifier
                                    .fillParentMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(8.dp),
                                    text = "Refresh Loading"
                                )
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        if (loadState?.append == LoadState.Loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        if (loadState?.refresh is LoadState.Error || loadState?.append is LoadState.Error) {
                            val isPaginatingError =
                                (loadState.append is LoadState.Error) || customers.itemCount > 1
                            val error = if (loadState.append is LoadState.Error)
                                (loadState.append as LoadState.Error).error
                            else
                                (loadState.refresh as LoadState.Error).error

                            val modifier = if (isPaginatingError) {
                                Modifier.padding(8.dp)
                            } else {
                                Modifier.fillParentMaxSize()
                            }
                            Column(
                                modifier = modifier,
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                if (!isPaginatingError) {
                                    Icon(
                                        modifier = Modifier
                                            .size(64.dp),
                                        imageVector = Icons.Rounded.Warning,
                                        contentDescription = null
                                    )
                                }

                                Text(
                                    modifier = Modifier
                                        .padding(8.dp),
                                    text = error.message ?: error.toString(),
                                    textAlign = TextAlign.Center,
                                )

                                Button(
                                    onClick = {
                                        customers.refresh()
                                    },
                                    content = {
                                        Text(text = "Refresh")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = Color.White,
                                    )
                                )
                            }
                        }
                    }
                }
                PullRefreshIndicator(
                    refreshing,
                    pullRefreshState,
                    Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
/*
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
        //.focusable()
    )
    {
        val filteredList: List<CustomerMasterData>
        val searchedText = state.value.text

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
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                //.focusable(),
                navController = navController
            )
        }
    }

 */


@Preview
@Composable
fun PagedCustomersListPreview() {
    MastroAndroidTheme {
        /* CustomersPagedList(
             customersPagedMasterDataList = listOf(
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
             state = remember { mutableStateOf(TextFieldValue("")) },
             modifier = Modifier.padding(8.dp),
             navController = NavController(LocalContext.current)
         )*/
    }
}