package com.mastrosql.app.ui.navigation.main.articlesscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.articles.ArticlesRepository
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface ArticlesUiState {
    data class Success(
        val articlesList: List<Article>
    ) : ArticlesUiState

    data class Error(val exception: Exception) : ArticlesUiState
    data object Loading : ArticlesUiState
}

/**
 * Factory for [ArticlesViewModel] that takes [ArticlesRepository] as a dependency
 */

class ArticlesViewModel(
    private val articlesRepository: ArticlesRepository,
) : ViewModel() {

    var articlesUiState: ArticlesUiState by mutableStateOf(ArticlesUiState.Loading)
        private set

    init {
        getArticles()
    }

    /**
     * Gets Articles information from the MastroAndroid API Retrofit service and updates the
     * [Article] [List] [MutableList].
     */
    fun getArticles() {
        viewModelScope.launch {
            articlesUiState = ArticlesUiState.Loading
            articlesUiState =
                try {
                    val articlesListResult =
                        articlesRepository.getArticles().items

                    // Trim all strings in the list
                    //Not used because it is already done in the API (see the articlesView)
                    /*val trimmedArticlesList =
                        articlesListResult.map { it.trimAllStrings() }
                    ArticlesUiState.Success(trimmedArticlesList)*/

                    ArticlesUiState.Success(articlesListResult)
                } catch (e: IOException) {
                    ArticlesUiState.Error(e)
                } catch (e: HttpException) {
                    ArticlesUiState.Error(e)
                } catch (e: Exception) {
                    ArticlesUiState.Error(e)
                }
        }
    }
}