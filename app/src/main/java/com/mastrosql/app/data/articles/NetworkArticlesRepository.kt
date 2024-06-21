package com.mastrosql.app.data.articles

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.JsonObject
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesDao
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import retrofit2.Response

/**
 * Network and database Implementation of Repository that fetch articles data list from mastroAndroidApi.
 */

/**
 * Repository for managing articles from network and local database.
 */
class NetworkArticlesRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
    private val articlesDao: ArticlesDao,
    context: Context
) : ArticlesRepository {

    /**
     * Updates the Mastro Android API service instance.
     */
    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    // set context as application context from parameter passed
    private val workManager = WorkManager.getInstance(context)

    /**
     * A flow emitting the first WorkInfo tagged with TAG_OUTPUT, if available.
     */
    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */

    override suspend fun getArticles(): ArticlesResponse =
        mastroAndroidApiService.getAllArticles()

    /**
     * Returns a stream of all articles.
     */
    override fun getAllArticlesStream(): Flow<List<Article>> {
        TODO("Not yet implemented")
    }

    /**
     * Returns a stream of an article by its ID.
     */
    override fun getArticlesStream(id: Int): Flow<Article?> {
        TODO("Not yet implemented")
    }

    /**
     * Inserts an article into the repository.
     */
    override suspend fun insertArticle(article: Article) {
        TODO("Not yet implemented")
    }

    /**
     * Deletes an article from the repository.
     */
    override suspend fun deleteArticle(article: Article) {
        TODO("Not yet implemented")
    }

    /**
     * Updates an article in the repository.
     */
    override suspend fun updateArticle(article: Article) {
        TODO("Not yet implemented")
    }

    /**
     * Inserts an article into a document.
     */
    //override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) { }
    override suspend fun insertArticleIntoDocument(
        documentId: Int,
        documentType: String,
        articleId: Int
    ): Response<JsonObject> {
        val body = JsonObject().apply {
            addProperty("documentId", documentId)
            addProperty("documentType", documentType)
            addProperty("articleId", articleId)
        }
        return mastroAndroidApiService.insertArticleIntoDocument(body)
    }

}