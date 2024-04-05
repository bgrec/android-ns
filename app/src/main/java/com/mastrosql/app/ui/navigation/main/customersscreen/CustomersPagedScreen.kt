package com.mastrosql.app.ui.navigation.main.customersscreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents.CustomersPagedList
import com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents.CustomersSearchView

@Composable
fun CustomersPagedScreen(
    drawerState: DrawerState,
    navController: NavController,
    viewModel: CustomersPagedMasterDataViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val customers = viewModel.getPagedCustomerMasterData().collectAsLazyPagingItems()

    Scaffold(topBar = {
        AppBar(
            drawerState = drawerState, title = R.string.drawer_customers
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textState = remember { mutableStateOf(TextFieldValue("")) }
            CustomersSearchView(state = textState)
            Log.i("CustomersMasterDat", "CustomersMasterDataViewModel created!")
            CustomersPagedList(
                customers = customers,
                modifier = Modifier,
                navController = navController,
                drawerState = drawerState
            )
        }
    }
}

/*/**
* The preview function should be responsible for creating the fake data and passing it to the
* function that displays it.
*/
// create list of fake data for preview
val fakeData = List(10) { "preview item $it" }
// create pagingData from a list of fake data
val pagingData = PagingData.from(fakeData)
// pass pagingData containing fake data to a MutableStateFlow
val fakeDataFlow = MutableStateFlow(pagingData)
// pass flow to composable
DisplayPaging(flow = fakeDataFlow)*/

/*
LazyColumn(
modifier = Modifier
.fillMaxSize()
//.padding(8.dp)
)
{
items(count = customers.itemCount) { index ->
customers[index]?.let {
Row(
horizontalArrangement = Arrangement.Center,
verticalAlignment = Alignment.CenterVertically,
) {
//if (customer.posterPath != null) {
if (it.businessName != null) {
var isImageLoading by remember { mutableStateOf(false) }

val painter = rememberAsyncImagePainter(
//model = "https://image.tmdb.org/t/p/w154" + customer.posterPath,
model = "https://image.tmdb.org/t/p/w154" + it.businessName,
)

isImageLoading = when (painter.state) {
is AsyncImagePainter.State.Loading -> true
else -> false
}

Box(
contentAlignment = Alignment.Center
) {
Image(
modifier = Modifier
.padding(horizontal = 6.dp, vertical = 3.dp)
.height(115.dp)
.width(77.dp)
.clip(RoundedCornerShape(8.dp)),
painter = painter,
contentDescription = "Poster Image",
contentScale = ContentScale.FillBounds,
)

if (isImageLoading) {
CircularProgressIndicator(
modifier = Modifier
.padding(horizontal = 6.dp, vertical = 3.dp),
color = MaterialTheme.colorScheme.primary,
)
}
}
}
Text(
modifier = Modifier
.padding(vertical = 18.dp, horizontal = 8.dp),
text = (index.toString() + ' ' + it.businessName + '-' + it.page.toString())
?: ""
)
}
Divider()
}
}
 */
