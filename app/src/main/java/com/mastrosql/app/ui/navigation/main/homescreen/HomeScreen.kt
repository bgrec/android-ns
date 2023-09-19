package com.mastrosql.app.ui.navigation.main.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.mastrosql.app.ui.navigation.main.cartScreen.CartViewModel
import com.mastrosql.app.data.datasource.DataSourceTest
import com.mastrosql.app.ui.components.bottombar.BottomBar
import com.mastrosql.app.ui.navigation.main.itemsScreen.itemComponents.ItemsList
import com.mastrosql.app.ui.navigation.main.itemsScreen.itemComponents.ItemsSearchSuggestionList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
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
            BottomBar(drawerState = drawerState, navController = navController)
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

@OptIn(ExperimentalComposeUiApi::class)
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
                itemsList = DataSourceTest().loadFilteredItemsByDescription(text),
                modifier = Modifier.padding(8.dp), //Modifier.offset(y = 80.dp)
                navController = navController,
                viewModel = viewModel


            )
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
}

@Preview
@Composable
fun HomeScreenPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
    HomeScreen(
        drawerState = DrawerState(DrawerValue.Closed),
        navController = NavController(LocalContext.current),
        viewModel = CartViewModel()
    )
}

