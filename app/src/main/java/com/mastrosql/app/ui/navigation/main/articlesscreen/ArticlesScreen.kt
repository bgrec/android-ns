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
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.components.appbar.AppBar
import com.mastrosql.app.ui.navigation.main.articlesscreen.articlescomponents.ArticlesList
import com.mastrosql.app.ui.navigation.main.articlesscreen.articlescomponents.SearchView
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
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
            modifier = modifier.fillMaxSize(),
            drawerState = drawerState,
            navController = navController,
            loading = true
        )

        is ArticlesUiState.Success -> ArticlesResultScreen(
            articlesUiState.articlesList,
            articlesUiState.documentType,
            articlesUiState.documentId,
            modifier = modifier.fillMaxWidth(),
            drawerState = drawerState,
            navController = navController
        ) { articleId ->
            viewModel.insertArticleIntoDocument(
                context = context,
                documentId = articlesUiState.documentId,
                documentType = articlesUiState.documentType,
                articleId = articleId,
                onInsertionComplete = {
                    navController.navigateUp()
                }
            )
        }

        is ArticlesUiState.Error -> ErrorScreen(
            articlesUiState.exception,
            viewModel::getArticles,
            modifier = modifier.fillMaxSize(),
            drawerState = drawerState,
            navController = navController
        )
    }

}

@ExperimentalMaterial3Api
@Composable
fun ArticlesResultScreen(
    articlesList: List<Article>,
    documentType: String?,
    documentId: Int?,
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    navController: NavController,
    onInsertArticleClick: (Int) -> Unit
) {
    //Change the backNavigationAction to null if you want to hide the back button
    var backNavigationAction: (() -> Unit)? = null
    if (documentId != null) {
        backNavigationAction = {
            navController.popBackStack()
        }
    }

    Scaffold(topBar = {
        AppBar(
            drawerState = drawerState, title = R.string.drawer_articles,
            showDrawerIconButton = documentId == null,
            backNavigationAction = backNavigationAction
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


@Preview
@Composable
fun ArticlesScreenPreview() {
    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
    ArticlesScreen(
        drawerState = DrawerState(DrawerValue.Closed),
        navController = NavController(LocalContext.current)
    )
}

//@Preview
//@Composable
//fun SearchBarPreview() {
//    //SearchBar(drawerState = DrawerState(DrawerValue.Closed))
//}

/*
*  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { MarsTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val marsViewModel: MarsViewModel = viewModel()
            HomeScreen(marsUiState = marsViewModel.marsUiState)
        }
    }
}

@Composable
fun MarsTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        modifier = modifier
    )
}
*
* */


/**
 * Versione con SearchBar diversa
 *
 * @ExperimentalMaterial3Api
 * @Composable
 * fun ArticlesResultScreen(
 *     customerMasterDataList: List<CustomerMasterData>,
 *     modifier: Modifier = Modifier,
 *     drawerState: DrawerState,
 *     navController: NavController
 * ) {
 *     Scaffold(
 *         topBar = {
 *             //AppBar(drawerState = drawerState)
 *             SearchBar(
 *                 customerMasterDataList = customerMasterDataList,
 *                 navController = navController
 *             )
 *         },
 *         bottomBar = {
 *             // BottomBar(drawerState = drawerState, navController = navController)
 *         },
 *     ) {
 *         Surface(
 *             modifier = Modifier
 *                 .fillMaxSize()
 *                 .padding(it)
 *         ) {
 *         }
 *     }
 * }
 */