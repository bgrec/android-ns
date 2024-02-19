package com.mastrosql.app.data.customers.paged

import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse

/**
 * Network and database Implementation of Repository that fetch customers data list from mastroAndroidApi.
 */

class NetworkDbCustomersPagedMasterDataRepository(
    private val mastroAndroidApiService: MastroAndroidApiService
) : CustomersPagedMasterDataRepository {

    override fun getApiService(): MastroAndroidApiService = mastroAndroidApiService

    /**
     * Fetches list of CustomersMasterData from mastroAndroidApi
     * */
    override suspend fun getPagedCustomersMasterData(
        offset: Int,
        limit: Int
    ): CustomersMasterDataResponse =
        mastroAndroidApiService.getCustomersMasterDataPage(offset, limit)
}

/*
override fun getSearchResultStream(query: String): Flow<PagingData<CustomerMasterData>> {
        Log.d("CustomersRepository", "New query: $query")

        // appending '%' so we can allow other characters to be before and after the query string
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory =
            { database.customersMasterDataDao().getCustomersByBusinessName(dbQuery) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            remoteMediator = CustomersRemoteMediator(
                query,
                database,
                mastroAndroidApiService
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
 */