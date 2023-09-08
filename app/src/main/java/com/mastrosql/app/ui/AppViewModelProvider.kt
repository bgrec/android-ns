package com.mastrosql.app.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mastrosql.app.MastroAndroidApplication
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemEntryViewModel
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemDetailsViewModel


/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            ItemEditViewModel(
                this.createSavedStateHandle(),
                mastroAndroidApplication().container.itemsRepository
            )
        }
        // Initializer for ItemEntryViewModel
        initializer {
            ItemEntryViewModel(mastroAndroidApplication().container.itemsRepository)
        }

        // Initializer for ItemDetailsViewModel
        initializer {
            ItemDetailsViewModel(
                this.createSavedStateHandle(),
                mastroAndroidApplication().container.itemsRepository
            )
        }

        // Initializer for HomeViewModel
        initializer {
            ItemsViewModel(mastroAndroidApplication().container.itemsRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.mastroAndroidApplication(): MastroAndroidApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MastroAndroidApplication)

/*
*  companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MastroAndroidApplication)
                val customersMasterDataRepository =
                    application.container.customersMasterDataRepository
                CustomersMasterDataViewModel(customersMasterDataRepository = customersMasterDataRepository)
            }
        }
    }
    * */