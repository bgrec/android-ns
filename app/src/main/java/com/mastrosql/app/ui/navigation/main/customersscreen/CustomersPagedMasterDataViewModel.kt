package com.mastrosql.app.ui.navigation.main.customersscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mastrosql.app.data.customers.paged.CustomersPagedMasterDataRepository
import com.mastrosql.app.data.customers.paged.CustomersRemoteMediator
import com.mastrosql.app.data.local.database.AppDatabase
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest


private const val DEFAULT_QUERY = ""
private const val CUSTOMERS_DEFAULT_LIMIT = 100


/*@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val moviesApiService: MoviesApiService,
    private val moviesDatabase: MoviesDatabase,
): ViewModel() {*/

/**
 * Factory for [CustomersPagedMasterDataViewModel] that takes [CustomersPagedMasterDataRepository] as a dependency
 */

class CustomersPagedMasterDataViewModel(
    private val customersPagedMasterDataRepository: CustomersPagedMasterDataRepository,
    private val appDatabase: AppDatabase
) : ViewModel() {


    private val queryFlow = MutableStateFlow("")

    fun onQueryChanged(query: String) {
        queryFlow.value = query
    }

    val querySearchResults = queryFlow.flatMapLatest { query ->
        // The database query returns a Flow which is output through
        // querySearchResults
        appDatabase.customersMasterDataDao().getCustomersByBusinessName(query)
    }

//    init {
//        //getPagedCustomerMasterData()
//    }

    @OptIn(ExperimentalPagingApi::class)
    fun getPagedCustomerMasterData(): Flow<PagingData<CustomerMasterData>> =
        Pager(
            config = PagingConfig(
                pageSize = CUSTOMERS_DEFAULT_LIMIT,
                prefetchDistance = CUSTOMERS_DEFAULT_LIMIT,
                initialLoadSize = CUSTOMERS_DEFAULT_LIMIT,
            ),
            pagingSourceFactory = {
                appDatabase.customersMasterDataDao().getPagedCustomers()
            },
            remoteMediator = CustomersRemoteMediator(
                DEFAULT_QUERY,
                appDatabase,
                customersPagedMasterDataRepository.getApiService()
            )
        ).flow
            /*
                pager.flow // Type is Flow<PagingData<User>>.
                // Map the outer stream so that the transformations are applied to
                // each new generation of PagingData.
                .map { pagingData ->
                // Transformations in this block are applied to the items
                // in the paged data.
                }
             */
            .cachedIn(viewModelScope)
}

//val state: StateFlow<UiState>

//private val pagingDataFlow: Flow<PagingData<UiModel>>

/**
 * Processor of side effects from the UI which in turn feedback into
 */
//private val accept: (UiAction) -> Unit

/** The mutable State that stores the status of the most recent request */


/*val customerPagingItems: Flow<PagingData<CustomerMasterData>> =
    Pager(PagingConfig(pageSize = 20)) {
        Flow { emit(customersMasterDataRepository.getCustomersMasterDataFromDb(page, pageSize)) }
    }.flow.cachedIn(viewModelScope)
*/

/*
sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)

sealed class UiModel {
    data class CustomerItem(val customer: CustomerMasterData) : UiModel()
    data class SeparatorItem(val description: String) : UiModel()
}

private val UiModel.CustomerItem.roundedStarCount: Int
    get() = this.customer.id / 10_000

private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "Android"
*/

/*
*  init {
        getCustomersMasterData()
        //----
        /*
        val initialQuery: String = savedStateHandle[LAST_SEARCH_QUERY] ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle[LAST_QUERY_SCROLLED] ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }

        pagingDataFlow = searches
            .flatMapLatest { searchCustomer(queryString = it.query) }
            .cachedIn(viewModelScope)

        state = combine(
            searches,
            queriesScrolled,
            ::Pair
        ).map { (search, scroll) ->
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }

         */
    }

    /*
    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }

    /*
    * pager.flow // Type is Flow<PagingData<User>>.
  .map { pagingData ->
    pagingData.map { user -> UiModel(user) }
  }
   */

    /*
    * pager.flow // Type is Flow<PagingData<User>>.
  .map { pagingData ->
    pagingData.filter { user -> !user.hiddenFromUi }
  }*/
    private fun searchCustomer(queryString: String): Flow<PagingData<UiModel>> =
        customersMasterDataRepository.getSearchResultStream(queryString)
            .map { pagingData -> pagingData.map { UiModel.CustomerItem(it) } }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }

                    if (before == null) {
                        // we're at the beginning of the list
                        return@insertSeparators UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                    }
                    // check between 2 items
                    if (before.roundedStarCount > after.roundedStarCount) {
                        if (after.roundedStarCount >= 1) {
                            UiModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                        } else {
                            UiModel.SeparatorItem("< 10.000+ stars")
                        }
                    } else {
                        // no separator
                        null
                    }
                }
            }
*/
*/

