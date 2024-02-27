package com.mastrosql.app.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mastrosql.app.MastroAndroidApplication
import com.mastrosql.app.data.local.database.AppDatabase
import com.mastrosql.app.ui.navigation.AppNavigationViewModel
import com.mastrosql.app.ui.navigation.UserPreferencesViewModel
import com.mastrosql.app.ui.navigation.main.articlesscreen.ArticlesViewModel
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersMasterDataViewModel
import com.mastrosql.app.ui.navigation.main.customersscreen.CustomersPagedMasterDataViewModel
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemDetailsViewModel
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemEditViewModel
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemEntryViewModel
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemsViewModel
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

        // Initializer for AppNavigationViewModel
        initializer {
            AppNavigationViewModel()
        }

        // Initializer for UserPreferencesViewModel
        initializer {
            UserPreferencesViewModel(
                mastroAndroidApplication().appContainer.userPreferencesRepository
            )
        }

    }

    // Define CompositionLocal for AppNavigationViewModel
    val LocalAppNavigationViewModelProvider = staticCompositionLocalOf<AppNavigationViewModel> {
        error("No AppNavigationViewModel provided")
        //TODO: Add a proper error message here and handle it
    }

    // Function to provide AppNavigationViewModel
    @Composable
    fun ProvideAppNavigationViewModel(content: @Composable () -> Unit) {
        val appNavigationViewModel = remember { AppNavigationViewModel() }
        CompositionLocalProvider(LocalAppNavigationViewModelProvider provides appNavigationViewModel) {
            content()
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [MastroAndroidApplication].
 */
private fun CreationExtras.mastroAndroidApplication(): MastroAndroidApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MastroAndroidApplication)

/*
companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MastroAndroidApplication)
                val customersMasterDataRepository =
                    application.container.customersMasterDataRepository
                CustomersMasterDataViewModel(customersMasterDataRepository = customersMasterDataRepository)
            }
        }
    }

*/