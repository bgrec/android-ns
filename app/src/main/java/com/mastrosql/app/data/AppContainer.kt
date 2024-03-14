package com.mastrosql.app.data

import RetrofitInstance
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
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
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.data.item.ItemsRepository
import com.mastrosql.app.data.item.OfflineItemsRepository
import com.mastrosql.app.data.local.UserPreferencesKeys
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.data.local.database.AppDatabase
import com.mastrosql.app.data.orders.NetworkOrdersRepository
import com.mastrosql.app.data.orders.OrdersRepository
import com.mastrosql.app.data.orders.orderdetails.NetworkOrderDetailsRepository
import com.mastrosql.app.data.orders.orderdetails.OrderDetailsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL

/**
 * Dependency Injection container at the application level.
 * This is used to provide a central location for the storage and retrieval of objects.
 *
 * App container for Dependency injection.
 *
 */

interface AppContainer {

    val customersMasterDataRepository: CustomersMasterDataRepository
    val customersPagedMasterDataRepository: CustomersPagedMasterDataRepository
    val customerMasterDataWorkManagerRepository: CustomersMasterDataRepository
    val itemsRepository: ItemsRepository
    val articlesRepository: ArticlesRepository
    val ordersRepository: OrdersRepository
    val orderDetailsRepository: OrderDetailsRepository
    val userPreferencesRepository: UserPreferencesRepository

    val mastroAndroidApiService: MastroAndroidApiService
    fun updateRetrofitService(newBaseUrl: String)

}

/**
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */

private const val APP_PREFERENCES_SETTINGS = "mastroandroid_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_PREFERENCES_SETTINGS
)

/**
 * Base URL for the MastroAndroid API
 */

class DefaultAppContainer(private val context: Context) : AppContainer {

    override lateinit var mastroAndroidApiService: MastroAndroidApiService

    private val defaultBaseUrl = if (isDevBuild()) {
        if (isValidUrl(BuildConfig.API_URL)) {
            BuildConfig.API_URL
        } else {
            Log.e("AppContainer", "BuildConfig.API_URL is not a valid URL. Using default URL.")
            "https://nipeservice.com"
        }
    } else {
        // Use a default value for release builds
        "https://nipeservice.com"
    }

    init {
        initializeRetrofitService()
    }

    private fun initializeRetrofitService() {
        val baseUrl = runBlocking { readBaseUrlFromDataStore(context) }
        mastroAndroidApiService = if (isValidUrl(baseUrl)) {
            RetrofitInstance.getRetrofitInstance(baseUrl)
        } else {
            RetrofitInstance.getRetrofitInstance(defaultBaseUrl)
        }
    }

    override fun updateRetrofitService(newBaseUrl: String) {
        //Update the retrofit service in all the repositories
        if (isValidUrl(newBaseUrl) && newBaseUrl.endsWith("/") && newBaseUrl != "/") {
          
            mastroAndroidApiService = RetrofitInstance.updateBaseUrl(newBaseUrl)

            customersMasterDataRepository.updateMastroAndroidApiService(mastroAndroidApiService)
            customersPagedMasterDataRepository.updateMastroAndroidApiService(mastroAndroidApiService)
            customerMasterDataWorkManagerRepository.updateMastroAndroidApiService(
                mastroAndroidApiService
            )
            articlesRepository.updateMastroAndroidApiService(mastroAndroidApiService)
            ordersRepository.updateMastroAndroidApiService(mastroAndroidApiService)
            orderDetailsRepository.updateMastroAndroidApiService(mastroAndroidApiService)
            userPreferencesRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        } else {
            Log.e("AppContainer", "Invalid base URL provided: $newBaseUrl")
        }
    }

    // Function to check if the build is .dev
    private fun isDevBuild(): Boolean {
        return BuildConfig.BUILD_TYPE == "debug"
    }

    // Function to read BASE_URL from datastore
    private suspend fun readBaseUrlFromDataStore(context: Context): String {
        val dataStore: DataStore<Preferences> = context.dataStore
        return try {
            val preferences = dataStore.data.first()
            preferences[UserPreferencesKeys.BASE_URL] ?: defaultBaseUrl
        } catch (e: IOException) {
            Log.d("baseUrl", "IOException while reading BASE_URL from DataStore: ${e.message}")
            defaultBaseUrl
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            URL(url).toURI()
            true
        } catch (e: MalformedURLException) {
            false
        } catch (e: URISyntaxException) {
            false
        }
    }

    override val customersMasterDataRepository: CustomersMasterDataRepository by lazy {

        NetworkCustomersMasterDataRepository(
            mastroAndroidApiService,
            AppDatabase.getInstance(context).customersMasterDataDao(),
            context
        )
        // trying with the worker version
        //WorkManagerCustomersMasterDataRepository(retrofitService, context)
    }

    /**
     * DI implementation for Customer Master Data repository
     */
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
        //OfflineOrdersRepository(AppDatabase.getInstance(context).articlesDao())
    }

    override val ordersRepository: OrdersRepository by lazy {
        NetworkOrdersRepository(
            mastroAndroidApiService,
            AppDatabase.getInstance(context).ordersDao(),
            context
        )
        //OfflineOrdersRepository(AppDatabase.getInstance(context).articlesDao())
    }

    override val orderDetailsRepository: OrderDetailsRepository by lazy {
        NetworkOrderDetailsRepository(
            mastroAndroidApiService,
            AppDatabase.getInstance(context).orderDetailsDao(),
            context
        )
        //OfflineOrdersRepository(AppDatabase.getInstance(context).articlesDao())
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

    /*private val retrofitService: MastroAndroidApiService by lazy {
        RetrofitInstance.getRetrofitInstance(baseUrl)
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