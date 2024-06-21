package com.mastrosql.app.ui.navigation.main.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import com.mastrosql.app.data.datasource.*
import com.mastrosql.app.ui.components.bottombar.*
import com.mastrosql.app.ui.navigation.main.cartscreen.*
import com.mastrosql.app.ui.navigation.main.itemsScreen.itemComponents.*


/**
 *
 */
@ExperimentalMaterial3Api
@Composable
fun OldHomeScreen(
    drawerState: DrawerState,
    navController: NavController,
    viewModel: CartViewModel
) {
    Scaffold(
        topBar = {
            //AppBar(drawerState = drawerState)
            SearchBar(
                drawerState = drawerState,
                navController = navController,
                viewModel = viewModel
            )
        },
        bottomBar = {
            BottomBar(/*drawerState = drawerState, */navController = navController)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Home")
        }
    }
}

/**
 *
 */
@ExperimentalMaterial3Api
@Composable
fun SearchBar(drawerState: DrawerState, navController: NavController, viewModel: CartViewModel) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    //val list = List(100) { "Text $it" }


    Box(Modifier.fillMaxSize()) {
        // Talkback focus order sorts based on x and y position before considering z-index. The
        // extra Box with semantics and fillMaxWidth is a workaround to get the search bar to focus
        // before the content.
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .semantics { isTraversalGroup = true }
                    .zIndex(1f)
                    .fillMaxWidth()) {

                SearchBar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp),
                    query = text,
                    onQueryChange = {
                        text = it
                    },

                    onSearch = { active = false },
                    active = active,
                    onActiveChange = {
                        active = it
                    },
                    placeholder = { Text("Articolo") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                ) {
                    ItemsSearchSuggestionList(
                        // list = yourList,
                        text = text,
                        active = active,
                        onSuggestionSelected = { selectedText -> text = selectedText },
                        onActiveStateChanged = { newActiveState -> active = newActiveState }
                    )

                }
            }
            ItemsList(
                itemsList = DataSourceTest_cancel_it_Later().loadFilteredItemsByDescription(text),
                modifier = Modifier.padding(8.dp), //Modifier.offset(y = 80.dp)
                navController = navController,
                viewModel = viewModel


            )
        }
    }
}

/**
 *
 */
@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
}

/**
 * Preview for [OldHomeScreen]
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun HomeScreenOldPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
    OldHomeScreen(
        drawerState = DrawerState(DrawerValue.Closed),
        navController = NavController(LocalContext.current),
        viewModel = CartViewModel()
    )
}

