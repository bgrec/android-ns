package com.mastrosql.app.data.articles

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mastrosql.app.TAG_OUTPUT
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesDao
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * Network and database Implementation of Repository that fetch articles data list from mastroAndroidApi.
 */

class NetworkArticlesRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
    private val articlesDao: ArticlesDao,
    context: Context
) : ArticlesRepository {

    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    // set context as application context from parameter passed
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */

    override suspend fun getArticles(): ArticlesResponse =
        mastroAndroidApiService.getAllArticles()

    override fun getAllArticlesStream(): Flow<List<Article>> {
        TODO("Not yet implemented")
    }

    override fun getArticlesStream(id: Int): Flow<Article?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertArticle(article: Article) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteArticle(article: Article) {
        TODO("Not yet implemented")
    }

    override suspend fun updateArticle(article: Article) {
        TODO("Not yet implemented")
    }

    //override suspend fun insertOrUpdateCustomersMasterData(dataFromServer: CustomersMasterDataResponse) { }


}