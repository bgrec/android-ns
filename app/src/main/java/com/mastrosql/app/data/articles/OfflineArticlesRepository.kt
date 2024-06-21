package com.mastrosql.app.data.articles

import androidx.work.WorkInfo
import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesDao
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

/**
 * Repository for managing articles stored offline.
 */
class OfflineArticlesRepository(
    private val articleDao: ArticlesDao,
    override val outputWorkInfo: Flow<WorkInfo>
) : ArticlesRepository {

    /**
     * Retrieves articles from offline storage.
     */
    override suspend fun getArticles(): ArticlesResponse {
        TODO()
    }

    /**
     * Returns a flow of all articles stored in the DAO.
     */
    override fun getAllArticlesStream(): Flow<List<Article>> = articleDao.getAllArticles()

    /**
     * Retrieves a stream of an article by ID.
     */
    override fun getArticlesStream(id: Int): Flow<Article?> = articleDao.getArticleById(id)

    /**
     * Inserts an article into the DAO.
     */
    override suspend fun insertArticle(article: Article) = articleDao.insert(article)

    /**
     * Deletes an article from the DAO.
     */
    override suspend fun deleteArticle(article: Article) = articleDao.delete(article)

    /**
     * Updates an existing article in the DAO.
     */
    override suspend fun updateArticle(article: Article) = articleDao.update(article)

    /**
     * Updates the Mastro Android API service instance.
     */
    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        TODO("Not yet implemented")
    }

    /**
     * Inserts an article into a document.
     */
    override suspend fun insertArticleIntoDocument(
        documentId: Int,
        documentType: String,
        articleId: Int
    ): Response<JsonObject> {
        TODO("Not yet implemented")
    }

}