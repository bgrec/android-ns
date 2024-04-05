package com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalMaterial3Api
@Composable
fun SearchBarOld(customerMasterDataList: List<CustomerMasterData>, navController: NavController) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

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
                    placeholder = { Text(stringResource(R.string.businessName)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                ) {
                    CustomersSearchSuggestionList(
                        // list = yourList,
                        text = text,
                        active = active,
                        onSuggestionSelected = { selectedText -> text = selectedText },
                        onActiveStateChanged = { newActiveState -> active = newActiveState }
                    )

                }
            }
            CustomersList(
                customerMasterDataList = customerMasterDataList,
                searchedTextState = remember { mutableStateOf(TextFieldValue("")) },
                modifier = Modifier.padding(4.dp),
                navController = navController
            )
        }
    }
}