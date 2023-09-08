/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mastrosql.app.rules

import com.mastrosql.app.ui.navigation.main.customersScreen.CustomersMasterDataViewModel
import com.mastrosql.app.ui.navigation.main.customersScreen.CustomersUiState
import com.mastrosql.app.fake.FakeDataSource
import com.mastrosql.app.fake.FakeNetworkCustomersMasterDataRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CustomersMasterDataViewModelTest {

    @get:Rule
    val testDispatcher = TestDispatcherRule()

    @Test
    fun customersMasterDataViewModel_getCustomersMasterData_verifyCustomersUiStateSuccess() =
        runTest {
            val customersMasterDataViewModel = CustomersMasterDataViewModel(
                customersMasterDataRepository = FakeNetworkCustomersMasterDataRepository()
            )
            assertEquals(
                CustomersUiState.Success(FakeDataSource.customersMasterDataResponse.items),
                customersMasterDataViewModel.customersUiState
            )
        }
}
