package com.mastrosql.app.ui.navigation.main.warehousescreen.inbound

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.main.articlesscreen.ArticlesResultScreen
import com.mastrosql.app.ui.navigation.main.articlesscreen.ArticlesUiState
import com.mastrosql.app.ui.navigation.main.articlesscreen.ArticlesViewModel
import com.mastrosql.app.ui.navigation.main.errorScreen.ErrorScreen
import com.mastrosql.app.ui.navigation.main.loadingscreen.LoadingScreen

/**
 *
 */
@ExperimentalMaterial3Api
@Composable
fun WarehouseInOperationsScreen(
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
