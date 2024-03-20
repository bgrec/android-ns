package com.mastrosql.app.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.mastrosql.app.BuildConfig
import com.mastrosql.app.data.articles.ArticlesRepository
import com.mastrosql.app.data.articles.NetworkArticlesRepository
import com.mastrosql.app.data.customers.CustomersMasterDataRepository
import com.mastrosql.app.data.customers.NetworkCustomersMasterDataRepository
import com.mastrosql.app.data.customers.paged.CustomersPagedMasterDataRepository
import com.mastrosql.app.data.customers.paged.NetworkDbCustomersPagedMasterDataRepository
import com.mastrosql.app.data.customers.workmanager.WorkManagerCustomersMasterDataRepository
import com.mastrosql.app.data.datasource.network.AppCookieJar
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.data.datasource.network.RetrofitInstance
import com.mastrosql.app.data.datasource.network.SessionManager
import com.mastrosql.app.data.item.ItemsRepository
import com.mastrosql.app.data.item.OfflineItemsRepository
import com.mastrosql.app.data.local.UserPreferencesKeys
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.data.local.database.AppDatabase
import com.mastrosql.app.data.login.LoginRepository
import com.mastrosql.app.data.login.NetworkLoginRepository
import com.mastrosql.app.data.orders.NetworkOrdersRepository
import com.mastrosql.app.data.orders.OrdersRepository
import com.mastrosql.app.data.orders.orderdetails.NetworkOrderDetailsRepository
import com.mastrosql.app.data.orders.orderdetails.OrderDetailsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.net.URL

/**
 * Dependency Injection container at the application level.
 * This is used to provide a central location for the storage and retrieval of objects.
 *
 * App container for Dependency injection.
 *
 */
interface AppContainer {
    val loginRepository: LoginRepository
    val customersMasterDataRepository: CustomersMasterDataRepository
    val customersPagedMasterDataRepository: CustomersPagedMasterDataRepository
    val customerMasterDataWorkManagerRepository: CustomersMasterDataRepository
    val itemsRepository: ItemsRepository
    val articlesRepository: ArticlesRepository
    val ordersRepository: OrdersRepository
    val orderDetailsRepository: OrderDetailsRepository
    val userPreferencesRepository: UserPreferencesRepository
    val mastroAndroidApiService: MastroAndroidApiService
    val sessionManager: SessionManager

    fun updateRetrofitService(newBaseUrl: String)
}

private const val APP_PREFERENCES_SETTINGS = "mastroandroid_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_PREFERENCES_SETTINGS
)

class DefaultAppContainer(private val context: Context) : AppContainer {

    override lateinit var mastroAndroidApiService: MastroAndroidApiService
    override lateinit var sessionManager: SessionManager

    private val defaultBaseUrl = if (isDevBuild()) {
        BuildConfig.API_URL.takeIf { isValidUrl(it) }
    } else {
        "https://nipeservice.com"
    }

    init {
        initializeRetrofitService()
    }

    private fun initializeRetrofitService() {
        val baseUrl = runBlocking { readBaseUrlFromDataStore(context) }

        AppCookieJar.setDataStore(context.dataStore)

        mastroAndroidApiService = if (isValidUrl(baseUrl)) {
            RetrofitInstance.getRetrofitInstance(baseUrl)
        } else {
            RetrofitInstance.getRetrofitInstance(defaultBaseUrl)
        }
    }

    override fun updateRetrofitService(newBaseUrl: String) {
        if (isValidUrl(newBaseUrl) && newBaseUrl.endsWith("/") && newBaseUrl != "/") {
            mastroAndroidApiService = RetrofitInstance.updateBaseUrl(newBaseUrl)
            updateRepositories()
        } else {
            Log.e("AppContainer", "Invalid base URL provided: $newBaseUrl")
        }
    }

    private fun updateRepositories() {
        customersMasterDataRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        customersPagedMasterDataRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        customerMasterDataWorkManagerRepository.updateMastroAndroidApiService(
            mastroAndroidApiService
        )
        articlesRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        ordersRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        orderDetailsRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        userPreferencesRepository.updateMastroAndroidApiService(mastroAndroidApiService)
    }

    private fun isDevBuild() = BuildConfig.BUILD_TYPE == "debug"

    private suspend fun readBaseUrlFromDataStore(context: Context): String {
        return try {
            context.dataStore.data.first()[UserPreferencesKeys.BASE_URL] ?: defaultBaseUrl!!
        } catch (e: Exception) {
            Log.d("baseUrl", "Exception while reading BASE_URL from DataStore: ${e.message}")
            defaultBaseUrl!!
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url).toURI()
            true
        } catch (e: Exception) {
            false
        }
    }

    override val loginRepository: NetworkLoginRepository by lazy {
        NetworkLoginRepository(mastroAndroidApiService)
    }

    override val customersMasterDataRepository: CustomersMasterDataRepository by lazy {
        NetworkCustomersMasterDataRepository(
            mastroAndroidApiService,
            AppDatabase.getInstance(context).customersMasterDataDao(),
            context
        )
    }

    override val customersPagedMasterDataRepository: CustomersPagedMasterDataRepository by lazy {
        NetworkDbCustomersPagedMasterDataRepository(mastroAndroidApiService)
    }

    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(AppDatabase.getInstance(context).itemDao())
    }

    override val articlesRepository: ArticlesRepository by lazy {
        NetworkArticlesRepository(
            mastroAndroidApiService,
            AppDatabase.getInstance(context).articlesDao(),
            context
        )
    }

    override val ordersRepository: OrdersRepository by lazy {
        NetworkOrdersRepository(
            mastroAndroidApiService,
            AppDatabase.getInstance(context).ordersDao(),
            context
        )
    }

    override val orderDetailsRepository: OrderDetailsRepository by lazy {
        NetworkOrderDetailsRepository(
            mastroAndroidApiService,
            AppDatabase.getInstance(context).orderDetailsDao(),
            context
        )
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(
            appContainer = this,
            dataStore = context.dataStore,
            mastroAndroidApiService = mastroAndroidApiService
        )
    }

    override val customerMasterDataWorkManagerRepository: CustomersMasterDataRepository by lazy {
        WorkManagerCustomersMasterDataRepository(mastroAndroidApiService, context)
    }

    /*override var sessionManager: SessionManager by lazy {
        SessionManager(context.dataStore)
    }*/

    /*private val retrofitService: MastroAndroidApiService by lazy {
        com.mastrosql.app.data.RetrofitInstance.getRetrofitInstance(baseUrl)
    }*/

}


/*class ImgurApi private constructor() {

    private val imgurService: ImgurService

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        imgurService = retrofit.create(ImgurService::class.java)
    }

    fun uploadImage(imageUri: Uri): Call<PostImageResponse> {
        val imageFile = File(imageUri.path!!)
        val requestFile = RequestBody.create(MEDIA_TYPE_PNG, imageFile)
        val body = MultipartBody.Part.createFormData("image", "image.png", requestFile)
        return imgurService.postImage(body)
    }

    private class AuthInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val headers = request.headers().newBuilder()
                .add("Authorization", "Client-ID ${Constants.IMGUR_CLIENT_ID}")
                .build()
            val authenticatedRequest = request.newBuilder().headers(headers).build()
            return chain.proceed(authenticatedRequest)
        }
    }

    companion object {

        private val MEDIA_TYPE_PNG = MediaType.parse("image/png")
        val instance: Lazy<ImgurApi> = lazy { ImgurApi() }
    }
}
*/