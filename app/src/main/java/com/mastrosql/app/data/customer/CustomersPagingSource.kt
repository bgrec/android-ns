package com.mastrosql.app.data.customer

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.data.local.database.AppDatabase
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerMasterData
import retrofit2.HttpException
import java.io.IOException

private const val CUSTOMERS_STARTING_OFFSET = 0

class CustomersPagingSource (
    private val service: MastroAndroidApiService,
    //private val database: AppDatabase,
    private val query: String
) : PagingSource<Int, CustomerMasterData>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CustomerMasterData> {
        val offset  = params.key ?: CUSTOMERS_STARTING_OFFSET
        val loadSize = params.loadSize

        return try {
            val response = service.getCustomersMasterDataPage(offset, loadSize)
            val customersMasterData = response.items

            val nextOffset = if (customersMasterData.isEmpty()) {
                null
            } else {
                offset + loadSize
            }
            LoadResult.Page(
                data = customersMasterData,
                prevKey = if (offset == CUSTOMERS_STARTING_OFFSET) null else offset - loadSize,
                nextKey = nextOffset
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, CustomerMasterData>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


}