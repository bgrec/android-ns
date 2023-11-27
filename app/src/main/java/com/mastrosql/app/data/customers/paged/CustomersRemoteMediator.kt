package com.mastrosql.app.data.customers.paged

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.data.local.database.AppDatabase
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersRemoteKeys
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val CUSTOMERS_STARTING_OFFSET = 0
private const val CUSTOMERS_DEFAULT_LIMIT = 100
private const val CACHE_TIMEOUT = 0L  //1 hour

/**
 * Remote mediator to append to and prepend to the list of customers.
 * This class is used by the Paging library to load pages of data from the network and database.
 * The Paging library loads pages of data from the network into the database, and pages of data
 * from the database into the UI.
 */

/** page is the offset of the database key (id) to be used as the anchor for this query
 */

@OptIn(ExperimentalPagingApi::class)
class CustomersRemoteMediator(
    private val query: String,
    private val appDatabase: AppDatabase,
    private val networkService: MastroAndroidApiService
) : RemoteMediator<Int, CustomerMasterData>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(CACHE_TIMEOUT, TimeUnit.HOURS)

        return if (System.currentTimeMillis() - (appDatabase.customersRemoteKeysDao()
                .getCreationTime() ?: 0) < cacheTimeout && cacheTimeout != 0L
        ) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CustomerMasterData>
    ): MediatorResult {
        val loadKey: Int = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(CUSTOMERS_DEFAULT_LIMIT)
                    ?: CUSTOMERS_STARTING_OFFSET
                //CUSTOMERS_STARTING_OFFSET
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val apiResponse =
                networkService.getCustomersMasterDataPage(loadKey, CUSTOMERS_DEFAULT_LIMIT)

            //delay(1000L) //TODO For testing only!

            val customers = apiResponse.items
            val endOfPaginationReached = customers.isEmpty()

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    /**
                     * Clear customersRemoteKeysDao and customersMasterDataDao tables in the database
                     */
                    appDatabase.customersRemoteKeysDao().clearRemoteKeys()
                    appDatabase.customersMasterDataDao().deleteAll()
                }
                /**
                 * page is the offset
                 */

                val prevKey =
                    if (loadKey >= CUSTOMERS_DEFAULT_LIMIT) loadKey - CUSTOMERS_DEFAULT_LIMIT else null
                val nextKey =
                    if (endOfPaginationReached) null else loadKey + CUSTOMERS_DEFAULT_LIMIT
                val remoteKeys = customers.map {
                    CustomersRemoteKeys(
                        customerId = it.id,
                        prevKey = prevKey,
                        currentPage = loadKey,
                        nextKey = nextKey
                    )
                }
                appDatabase.customersRemoteKeysDao().insertAll(remoteKeys)
                /**
                 * Inserts the api offset too
                 */
                appDatabase.customersMasterDataDao()
                    .insertAll(customers.onEachIndexed { _, customer -> customer.page = loadKey })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (error: IOException) {
            return MediatorResult.Error(error)
        } catch (error: HttpException) {
            return MediatorResult.Error(error)
        }
    }

    /**
     * Returns the key (offset) of the item closest to the given position.
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, CustomerMasterData>): CustomersRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                appDatabase.customersRemoteKeysDao().getRemoteKeysByCustomerId(id)
            }
        }
    }

    /**
     * Returns the key (offset) of the first item in the list.
     */
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, CustomerMasterData>): CustomersRemoteKeys? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { customer ->
            appDatabase.customersRemoteKeysDao().getRemoteKeysByCustomerId(customer.id)
        }
    }

    /**
     * Returns the key (offset) of the last item in the list.
     */
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CustomerMasterData>): CustomersRemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { customer ->
            appDatabase.customersRemoteKeysDao().getRemoteKeysByCustomerId(customer.id)
        }
    }
}

/*
@OptIn(ExperimentalPagingApi::class)
class CustomersRemoteMediator(
    private val query: String,
    private val customersDatabase: AppDatabase,
    private val networkService: MastroAndroidApiService
) : RemoteMediator<Int, CustomerMasterData>() {
    //private val customerMasterDataDao = database.customersMasterDataDao()

    override suspend fun initialize(): InitializeAction {
        /*val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
        return if (System.currentTimeMillis() - database.customersMasterDataDao()
                .lastUpdated() <= cacheTimeout
        ) {
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning
            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }*/
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CustomerMasterData>
    ): MediatorResult {val offset = when (loadType) {
        LoadType.REFRESH -> {
            val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
            remoteKeys?.nextOffset?.minus(1) ?: CUSTOMERS_STARTING_OFFSET
        }
        LoadType.PREPEND -> {
            val remoteKeys = getRemoteKeyForFirstItem(state)
            // If remoteKeys is null, that means the refresh result is not in the database yet.
            // We can return Success with `endOfPaginationReached = false` because Paging
            // will call this method again if RemoteKeys becomes non-null.
            // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
            // the end of pagination for prepend.
            val prevOffset = remoteKeys?.prevOffset
            if (prevOffset == null) {
                return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            prevOffset
        }
        LoadType.APPEND -> {
            val remoteKeys = getRemoteKeyForLastItem(state)
            // If remoteKeys is null, that means the refresh result is not in the database yet.
            // We can return Success with `endOfPaginationReached = false` because Paging
            // will call this method again if RemoteKeys becomes non-null.
            // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
            // the end of pagination for append.
            val nextOffset = remoteKeys?.nextOffset
            if (nextOffset == null) {
                return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            nextOffset
        }
    }

        val apiQuery = query //+ IN_QUALIFIER

        try {
            val apiResponse = networkService.getCustomersMasterDataPage(offset = offset, 100 )

            val customers = apiResponse.items
            val endOfPaginationReached = customers.isEmpty()
            customersDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    customersDatabase.customersRemoteKeysDao().clearRemoteKeys()
                    customersDatabase.customersMasterDataDao().clearAll()
                }
                val prevOffset = if (offset == CUSTOMERS_STARTING_OFFSET) null else offset - apiResponse.limit
                val nextOffset = if (endOfPaginationReached) null else offset + apiResponse.limit
                val keys = customers.map {
                    CustomersRemoteKeys(id = it.id, prevOffset  = prevOffset, nextOffset = nextOffset)
                }
                customersDatabase.customersRemoteKeysDao().insertAll(keys)
                customersDatabase.customersMasterDataDao().insertAll(customers)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CustomerMasterData>): CustomersRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { customer ->
                // Get the remote keys of the last item retrieved
                customersDatabase.customersRemoteKeysDao().getRemoteKeysByCustomerId(customer.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, CustomerMasterData>): CustomersRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { customer ->
                // Get the remote keys of the first items retrieved
                customersDatabase.customersRemoteKeysDao().getRemoteKeysByCustomerId(customer.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, CustomerMasterData>
    ): CustomersRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                customersDatabase.customersRemoteKeysDao().getRemoteKeysByCustomerId(id = id)
            }
        }
    }
}

*/