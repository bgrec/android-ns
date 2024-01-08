package com.mastrosql.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
import com.mastrosql.app.data.local.UserPreferencesRepository
import com.mastrosql.app.data.local.database.AppDatabase
import com.mastrosql.app.data.orders.NetworkOrdersRepository
import com.mastrosql.app.data.orders.OrdersRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Dependency Injection container at the application level.
 *  App container for Dependency injection.
 */

interface AppContainer {

    val customersMasterDataRepository: CustomersMasterDataRepository
    val customersPagedMasterDataRepository: CustomersPagedMasterDataRepository
    val customerMasterDataWorkManagerRepository: CustomersMasterDataRepository
    val itemsRepository: ItemsRepository
    val articlesRepository: ArticlesRepository
    val ordersRepository: OrdersRepository
    val userPreferencesRepository: UserPreferencesRepository

}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */

private const val LAYOUT_PREFERENCE_NAME = "mastroandroid_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)

/**
 * Base URL for the MastroAndroid API
 */

private const val BASE_URL = "https://192.168.0.126:8444/backend/lmnew/"
//"https://android-kotlin-fun-mars-server.appspot.com/"


class DefaultAppContainer(private val context: Context) : AppContainer {


    // Custom TrustManager for testing purposes (not recommended for production)
    private val trustAllCertificates = arrayOf<TrustManager>(
        object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    )

    /**
     * SSL Context for testing purposes (not recommended for production)
     */
    private val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, trustAllCertificates, java.security.SecureRandom())
    }

    /**
     * SSL Socket Factory for testing purposes (not recommended for production)
     */
    private val sslSocketFactory = sslContext.socketFactory


    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */

    private val retrofit: Retrofit = Retrofit.Builder()
        // .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCertificates[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .build()

    /**
     * Retrofit service object for creating api calls
     */

    private val retrofitService: MastroAndroidApiService by lazy {
        retrofit.create(MastroAndroidApiService::class.java)
    }

    /**
     * DI implementation for Customer Master Data repository
     */
    override val customersMasterDataRepository: CustomersMasterDataRepository by lazy {

        NetworkCustomersMasterDataRepository(
            retrofitService,
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
        NetworkDbCustomersPagedMasterDataRepository(retrofitService)

    }

    /**
     * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
     */
    /**
     * Implementation for [ItemsRepository]
     */
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(AppDatabase.getInstance(context).itemDao())
    }

    override val articlesRepository: ArticlesRepository by lazy {
        NetworkArticlesRepository(
            retrofitService,
            AppDatabase.getInstance(context).articlesDao(),
            context
        )
        //OfflineOrdersRepository(AppDatabase.getInstance(context).articlesDao())
    }

    override val ordersRepository: OrdersRepository by lazy {
        NetworkOrdersRepository(
            retrofitService,
            AppDatabase.getInstance(context).ordersDao(),
            context
        )
        //OfflineOrdersRepository(AppDatabase.getInstance(context).articlesDao())
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(dataStore = context.dataStore)
    }

    override val customerMasterDataWorkManagerRepository: CustomersMasterDataRepository by lazy {
        WorkManagerCustomersMasterDataRepository(retrofitService, context)
    }


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