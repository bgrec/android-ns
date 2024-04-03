package com.mastrosql.app.data.articles

import androidx.work.WorkInfo
import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesDao
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class OfflineArticlesRepository(
    private val articleDao: ArticlesDao,
    override val outputWorkInfo: Flow<WorkInfo>
) : ArticlesRepository {
    override suspend fun getArticles(): ArticlesResponse {
        TODO()
    }

    override fun getAllArticlesStream(): Flow<List<Article>> = articleDao.getAllArticles()

    override fun getArticlesStream(id: Int): Flow<Article?> = articleDao.getArticleById(id)

    override suspend fun insertArticle(article: Article) = articleDao.insert(article)

    override suspend fun deleteArticle(article: Article) = articleDao.delete(article)

    override suspend fun updateArticle(article: Article) = articleDao.update(article)
    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        TODO("Not yet implemented")
    }

    override suspend fun insertArticleIntoDocument(
        documentId: Int,
        documentType: String,
        articleId: Int
    ): Response<JsonObject> {
        TODO("Not yet implemented")
    }

}