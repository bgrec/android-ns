package com.mastrosql.app.ui.navigation.main.articlesscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.scanner.ScanReceiver
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.navigation.main.articlesscreen.articlescomponents.ArticlesList
import com.mastrosql.app.ui.navigation.main.articlesscreen.articlescomponents.SearchView
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen

@ExperimentalMaterial3Api
@Composable
fun ArticlesScreen(
    drawerState: DrawerState,
    navController: NavController,
    viewModel: ArticlesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val articlesUiState = viewModel.articlesUiState
    val modifier = Modifier.fillMaxSize()
    val context = LocalContext.current

    when (articlesUiState) {
        is ArticlesUiState.Loading -> LoadingScreen(
            modifier = modifier.fillMaxSize(), loading = true
        )

        is ArticlesUiState.Success -> ArticlesResultScreen(
            modifier = modifier.fillMaxWidth(),
            articlesList = articlesUiState.articlesList,
            documentType = articlesUiState.documentType,
            documentId = articlesUiState.documentId,
            drawerState = drawerState,
            navController = navController
        ) { articleId ->
            viewModel.insertArticleIntoDocument(context = context,
                documentId = articlesUiState.documentId,
                documentType = articlesUiState.documentType,
                articleId = articleId,
                onInsertionComplete = {
                    navController.navigateUp()
                })
        }

        is ArticlesUiState.Error -> ErrorScreen(
            articlesUiState.exception,
            viewModel::getArticles,
            modifier = modifier.fillMaxSize(),
            navController = navController
        )
    }

}

/**
 * Screen to display the list of articles
 */
@ExperimentalMaterial3Api
@Composable
fun ArticlesResultScreen(
    modifier: Modifier = Modifier,
    articlesList: List<Article>,
    documentType: String?,
    documentId: Int?,
    drawerState: DrawerState,
    navController: NavController,
    onInsertArticleClick: (Int) -> Unit
) {
    // Get the context
    val context = LocalContext.current

    // Create a mutable state for the search text
    val textState = remember { mutableStateOf(TextFieldValue("")) }

    //Change the backNavigationAction to null if you want to hide the back button
    var backNavigationAction: (() -> Unit)? = null
    if (documentId != null) {
        backNavigationAction = {
            navController.popBackStack()
        }
    }

    // Create and register the ScanReceiver to listen for scanned codes from the scanner
    // The ScanReceiver is a BroadcastReceiver that listens
    // for the "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED" broadcast
    val scanReceiver = remember {
        ScanReceiver { barcode ->
            textState.value = TextFieldValue(barcode)
        }
    }

    // Register the receiver when the composable is first composed
    // and unregister it when the composable is disposed
    DisposableEffect(Unit) {
        // Register the ScanReceiver to listen for the scan broadcast
        scanReceiver.register(context)

        // Unregister the receiver when the composable is disposed
        onDispose {
            context.unregisterReceiver(scanReceiver)
        }
    }

    Scaffold(topBar = {
        AppBar(
            drawerState = drawerState,
            title = R.string.drawer_articles,
            showDrawerIconButton = documentId == null,
            backNavigationAction = backNavigationAction
        )
    }) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SearchView(state = textState)
            ArticlesList(
                articlesList = articlesList,
                documentId = documentId,
                documentType = documentType,
                state = textState,
                modifier = Modifier.padding(4.dp),
                navController = navController,
                onInsertArticleClick = onInsertArticleClick
            )
        }
    }
}


/**
 * Preview for [ArticlesScreen]
 */
@ExperimentalMaterial3Api
@Preview
@Composable
fun ArticlesScreenPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
    ArticlesScreen(
        drawerState = DrawerState(DrawerValue.Closed),
        navController = NavController(LocalContext.current)
    )
}
