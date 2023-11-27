package com.mastrosql.app.data.articles

import androidx.work.WorkInfo
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesDao
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import kotlinx.coroutines.flow.Flow

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
}