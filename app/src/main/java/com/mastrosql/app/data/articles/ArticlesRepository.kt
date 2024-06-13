package com.mastrosql.app.data.articles


import androidx.work.WorkInfo
import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

/**
 * Repository that provides insert, update, delete, and retrieve of [Article] from a given data source.
 */
interface ArticlesRepository {

    val outputWorkInfo: Flow<WorkInfo>
    suspend fun getArticles(): ArticlesResponse
    //suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse)

    /**
     * Retrieve all the Articles from the the given data source.
     */
    fun getAllArticlesStream(): Flow<List<Article>>

    /**
     * Retrieve an Article from the given data source that matches with the [id].
     */
    fun getArticlesStream(id: Int): Flow<Article?>

    /**
     * Insert Article in the data source
     */
    suspend fun insertArticle(article: Article)

    /**
     * Delete article from the data source
     */
    suspend fun deleteArticle(article: Article)

    /**
     * Update article in the data source
     */
    suspend fun updateArticle(article: Article)

    /**
     * Update the [MastroAndroidApiService] used by the repository.
     */
    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService)


    suspend fun insertArticleIntoDocument(
        documentId: Int, documentType: String, articleId: Int
    ): Response<JsonObject>
}
