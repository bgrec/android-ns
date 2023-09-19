package com.mastrosql.app.ui.navigation.main.itemsScreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.item.ItemsRepository
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Item
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class ItemsViewModel(itemsRepository: ItemsRepository) : ViewModel() {

    /**
     * Holds items ui state. The list of items are retrieved from [ItemsRepository] and mapped to
     * [ItemsUiState]
     */
    val itemsUiState: StateFlow<ItemsUiState> =
        itemsRepository.getAllItemsStream().map { ItemsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ItemsUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class ItemsUiState(val itemList: List<Item> = listOf())
