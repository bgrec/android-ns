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
import com.mastrosql.app.data.warehouse.outbound.NetworkWarehouseOutRepository
import com.mastrosql.app.data.warehouse.outbound.WarehouseOutRepository
import com.mastrosql.app.data.warehouse.outbound.whoutdetails.NetworkWhOutDetailsRepository
import com.mastrosql.app.data.warehouse.outbound.whoutdetails.WhOutDetailsRepository
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


@Suppress("KDocMissingDocumentation")
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
    val warehouseOutRepository: WarehouseOutRepository
    val whOutDetailsRepository: WhOutDetailsRepository
    val mastroAndroidApiService: MastroAndroidApiService

    /**
     * Updates the Retrofit service with a new base URL.
     *
     * This function reconfigures the Retrofit instance to use the provided [newBaseUrl],
     * updating all service endpoints to the new base URL.
     */
    fun updateRetrofitService(newBaseUrl: String)
}

private const val APP_PREFERENCES_SETTINGS = "mastroandroid_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_PREFERENCES_SETTINGS
)

/**
 * Default implementation of [AppContainer] that manages dependencies and services for the application.
 *
 * This class initializes and manages various repositories and Retrofit services based on the application's
 * configuration and runtime conditions. It provides methods to update the Retrofit base URL dynamically
 * and ensures that all dependent repositories and services are updated accordingly.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    override lateinit var mastroAndroidApiService: MastroAndroidApiService

    companion object {
        private const val DEFAULT_BASE_URL = "https://nipeservice.com"
    }

    private val defaultBaseUrl = if (isDevBuild()) {
        BuildConfig.API_URL.takeIf { isValidUrl(it) }
    } else {
        DEFAULT_BASE_URL
    }

    /**
     * Initializes the [AppContainer] instance by setting up the Retrofit service.
     *
     * When an instance of [AppContainer] is created, this block ensures that the Retrofit service,
     * [mastroAndroidApiService], is initialized using the stored or default base URL retrieved from the DataStore.
     */
    init {
        initializeRetrofitService()
    }

    /**
     * Initializes the Retrofit service [mastroAndroidApiService] based on the base URL retrieved from the DataStore.
     *
     * The function first retrieves the base URL asynchronously from the DataStore using [readBaseUrlFromDataStore].
     * Then, it sets the DataStore instance for [SessionManager]. Finally, it initializes [mastroAndroidApiService]
     * with a Retrofit instance configured with the retrieved base URL, or falls back to [defaultBaseUrl] if the
     * retrieved URL is invalid.
     */
    private fun initializeRetrofitService() {
        val baseUrl = runBlocking { readBaseUrlFromDataStore(context) }

        SessionManager.setDataStore(context.dataStore)

        mastroAndroidApiService = if (isValidUrl(baseUrl)) {
            RetrofitInstance.getRetrofitInstance(baseUrl)
        } else {
            RetrofitInstance.getRetrofitInstance(defaultBaseUrl)
        }
    }

    /**
     * Initializes the Retrofit service [mastroAndroidApiService] based on the base URL retrieved from the DataStore.
     *
     * This function:
     * 1. Asynchronously retrieves the base URL from the DataStore using [readBaseUrlFromDataStore].
     * 2. Sets the DataStore instance for [SessionManager] to manage session-related data.
     * 3. Initializes [mastroAndroidApiService] with a Retrofit instance configured with the retrieved base URL,
     *    or falls back to [defaultBaseUrl] if the retrieved URL is invalid or not provided.
     */
    override fun updateRetrofitService(newBaseUrl: String) {
        if (isValidUrl(newBaseUrl) && newBaseUrl.endsWith("/") && newBaseUrl != "/") {
            mastroAndroidApiService = RetrofitInstance.updateBaseUrl(newBaseUrl)
            updateRepositories()
        } else {
            Log.e("AppContainer", "Invalid base URL provided: $newBaseUrl")
        }
    }

    /**
     * Updates all repositories in the application with the provided [mastroAndroidApiService].
     * Each repository's instance of [MastroAndroidApiService] is updated to use the new service instance.
     * This ensures that all data fetching and network operations performed by repositories use
     * the latest configuration of [MastroAndroidApiService].
     */
    private fun updateRepositories() {
        loginRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        customersMasterDataRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        customersPagedMasterDataRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        customerMasterDataWorkManagerRepository.updateMastroAndroidApiService(
            mastroAndroidApiService
        )
        articlesRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        ordersRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        orderDetailsRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        userPreferencesRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        warehouseOutRepository.updateMastroAndroidApiService(mastroAndroidApiService)
        whOutDetailsRepository.updateMastroAndroidApiService(mastroAndroidApiService)
    }

    /**
     * Checks if the current build type of the application is "debug".
     * This is typically used to determine if the application is running in a development environment.
     */
    private fun isDevBuild() = BuildConfig.BUILD_TYPE == "debug"

    /**
     * Reads the base URL asynchronously from the DataStore associated with the provided [context].
     * If the base URL is not found in the DataStore, falls back to [defaultBaseUrl].
     */
    private suspend fun readBaseUrlFromDataStore(context: Context): String {

        /**
         * Retrieves the base URL from the DataStore associated with the provided [context].
         * If the base URL is not found in the DataStore, falls back to [defaultBaseUrl].
         */
        return try {
            context.dataStore.data.first()[UserPreferencesKeys.BASE_URL] ?: defaultBaseUrl!!
        }

        /**
         * Handles exceptions that occur while attempting to read the BASE_URL from the DataStore.
         * Logs an error message using Android's Log class with the tag "baseUrl" and the exception message.
         * Falls back to [defaultBaseUrl] if an exception occurs during the retrieval process.
         */
        catch (e: Exception) {
            Log.e("baseUrl", "Exception while reading BASE_URL from DataStore: ${e.message}")
            defaultBaseUrl!!
        }
    }

    /**
     * Checks if the provided URL string is a valid URL.
     *
     * This function attempts to create a URL object and convert it to a URI using Java's URL and URI classes.
     * If successful, it considers the URL as valid and returns true; otherwise, it catches any exceptions
     * thrown during the process and returns false.
     */
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
        OfflineItemsRepository(
            AppDatabase.getInstance(context).itemDao()
        )
    }

    override val articlesRepository: ArticlesRepository by lazy {
        NetworkArticlesRepository(
            mastroAndroidApiService, AppDatabase.getInstance(context).articlesDao(), context
        )
    }

    override val ordersRepository: OrdersRepository by lazy {
        NetworkOrdersRepository(
            mastroAndroidApiService, AppDatabase.getInstance(context).ordersDao(), context
        )
    }

    override val orderDetailsRepository: OrderDetailsRepository by lazy {
        NetworkOrderDetailsRepository(
            mastroAndroidApiService, AppDatabase.getInstance(context).orderDetailsDao(), context
        )
    }

    override val warehouseOutRepository: WarehouseOutRepository by lazy {
        NetworkWarehouseOutRepository(
            mastroAndroidApiService,
            AppDatabase.getInstance(context).warehouseOutboundDao(),
            context
        )
    }

    override val whOutDetailsRepository: WhOutDetailsRepository by lazy {
        NetworkWhOutDetailsRepository(
            mastroAndroidApiService, AppDatabase.getInstance(context).whOutDetailsDao(), context
        )
    }

    /**
     * Lazily initializes the [UserPreferencesRepository] instance
     * using the [context] and [mastroAndroidApiService].
     */
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
        com.mastrosql.app.data.RetrofitInstance.getRetrofitInstance(baseUrl)
    }*/

}
