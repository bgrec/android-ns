package com.mastrosql.app.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mastrosql.app.MastroAndroidApplication
import com.mastrosql.app.data.local.database.AppDatabase
import com.mastrosql.app.ui.navigation.main.settingsscreen.UserPreferencesViewModel
import com.mastrosql.app.ui.navigation.main.articlesscreen.ArticlesViewModel
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersMasterDataViewModel
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersPagedMasterDataViewModel
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemDetailsViewModel
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemEditViewModel
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemEntryViewModel
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemsViewModel
import com.mastrosql.app.ui.navigation.main.loginscreen.LoginViewModel
import com.mastrosql.app.ui.navigation.main.ordersscreen.OrdersViewModel
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetailsViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Mastro Android app
 */
object AppViewModelProvider {
    /**
     * Factory to create instance of ViewModel for the entire Mastro Android app
     */
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            ItemEditViewModel(
                this.createSavedStateHandle(),
                mastroAndroidApplication().appContainer.itemsRepository
            )
        }
        // Initializer for ItemEntryViewModel
        initializer {
            ItemEntryViewModel(mastroAndroidApplication().appContainer.itemsRepository)
        }

        // Initializer for ItemDetailsViewModel
        initializer {
            ItemDetailsViewModel(
                this.createSavedStateHandle(),
                mastroAndroidApplication().appContainer.itemsRepository
            )
        }

        // Initializer for ItemsViewModel
        initializer {
            ItemsViewModel(
                mastroAndroidApplication().appContainer.itemsRepository
            )
        }

        // Initializer for CustomersMasterDataViewModel
        initializer {
            CustomersMasterDataViewModel(
                mastroAndroidApplication().appContainer.customersMasterDataRepository
            )
        }

        // Initializer for CustomersPagedMasterDataViewModel
        initializer {
            CustomersPagedMasterDataViewModel(
                mastroAndroidApplication().appContainer.customersPagedMasterDataRepository,
                AppDatabase.getInstance(mastroAndroidApplication())
            )
        }

        // Initializer for ArticlesViewModel
        initializer {
            ArticlesViewModel(
                this.createSavedStateHandle(),
                mastroAndroidApplication().appContainer.articlesRepository
            )
        }

        // Initializer for OrdersViewModel
        initializer {
            OrdersViewModel(
                mastroAndroidApplication().appContainer.ordersRepository
            )
        }

        // Initializer for OrderDetailsViewModel
        initializer {
            OrderDetailsViewModel(
                this.createSavedStateHandle(),
                mastroAndroidApplication().appContainer.orderDetailsRepository,
            )
        }

        // Initializer for UserPreferencesViewModel
        initializer {
            UserPreferencesViewModel(
                mastroAndroidApplication().appContainer.userPreferencesRepository
            )
        }

        initializer {
            LoginViewModel(
                mastroAndroidApplication().appContainer.loginRepository,
                mastroAndroidApplication().appContainer.userPreferencesRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [MastroAndroidApplication].
 */
private fun CreationExtras.mastroAndroidApplication(): MastroAndroidApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MastroAndroidApplication)


//companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application = (this[APPLICATION_KEY] as MastroAndroidApplication)
//                val customersMasterDataRepository =
//                    application.container.customersMasterDataRepository
//                CustomersMasterDataViewModel(customersMasterDataRepository = customersMasterDataRepository)
//            }
//        }
//    }