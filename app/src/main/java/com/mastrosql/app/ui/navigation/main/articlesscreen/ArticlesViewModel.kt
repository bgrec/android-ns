package com.mastrosql.app.ui.navigation.main.articlesscreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.articles.ArticlesRepository
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface ArticlesUiState {
    data class Success(
        val articlesList: List<Article>,
        val documentId: Int? = null,
        val documentType: String? = null
    ) : ArticlesUiState

    data class Error(val exception: Exception) : ArticlesUiState
    data object Loading : ArticlesUiState
}

/**
 * Factory for [ArticlesViewModel] that takes [ArticlesRepository] as a dependency
 */

class ArticlesViewModel(
    savedStateHandle: SavedStateHandle,
    private val articlesRepository: ArticlesRepository,
) : ViewModel() {

    var articlesUiState: ArticlesUiState by mutableStateOf(ArticlesUiState.Loading)
        private set

    private val _documentId: MutableStateFlow<Int?> =
        MutableStateFlow(savedStateHandle["documentId"])
    private val documentId: StateFlow<Int?> = _documentId

    private val _documentType: MutableStateFlow<String?> =
        MutableStateFlow(savedStateHandle["documentType"])
    private val documentType: StateFlow<String?> = _documentType

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

                    ArticlesUiState.Success(
                        articlesListResult,
                        documentId.value,
                        documentType.value
                    )
                } catch (e: IOException) {
                    ArticlesUiState.Error(e)
                } catch (e: HttpException) {
                    ArticlesUiState.Error(e)
                } catch (e: Exception) {
                    ArticlesUiState.Error(e)
                }
        }
    }

    fun insertArticleIntoDocument(
        context: Context,
        documentId: Int?,
        documentType: String?,
        articleId: Int?,
        onInsertionComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (documentId == null || documentType == null || articleId == null) {
                    throw IllegalArgumentException("Document ID or Document Type or Article ID is null")
                }
                val response = articlesRepository.insertArticleIntoDocument(
                    documentId,
                    documentType,
                    articleId
                )

                // Handle the response status code
                withContext(Dispatchers.Main) {
                    when (response.code()) {
                        200 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "Articolo inserito con successo"
                            )

                            onInsertionComplete()
                        }


                        401 -> {
                            showToast(
                                context,
                                Toast.LENGTH_SHORT,
                                "Modifiche non salvate, non autorizzato"
                            )
                            articlesUiState = ArticlesUiState.Error(HttpException(response))
                        }

                        //TODO: Add other status codes and handle them
                        404 -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "Collegamento riuscito, api not trovata ${response.code()}"
                        )

                        else -> showToast(
                            context,
                            Toast.LENGTH_LONG,
                            "Errore api: ${response.code()}"
                        )
                    }
                }

            } catch (e: IOException) {
                // Handle IOException (e.g., network error)
                showToast(context, Toast.LENGTH_LONG, "Network error occurred: ${e.message}")
            } catch (e: HttpException) {
                // Handle HttpException (e.g., non-2xx response)
                showToast(context, Toast.LENGTH_LONG, "HTTP error occurred: ${e.message}")
            } catch (e: SocketTimeoutException) {
                // Handle socket timeout exception
                showToast(
                    context,
                    Toast.LENGTH_LONG,
                    "Connection timed out. Please try again later."
                )
            } catch (e: Exception) {
                // Handle generic exception
                showToast(context, Toast.LENGTH_LONG, "An unexpected error occurred: ${e.message}")
            } catch (e: IllegalArgumentException) {
                // Handle generic exception
                showToast(context, Toast.LENGTH_LONG, "An unexpected error occurred: ${e.message}")
            }
        }
    }

    private fun showToast(context: Context, toastLength: Int, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            if (message.isNotEmpty()) {
                val toast = Toast.makeText(context, message, toastLength)
                //Error for toast gravity with message
                //toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            } else {
                // Hide loading message by not showing any toast
            }
        }
    }

}