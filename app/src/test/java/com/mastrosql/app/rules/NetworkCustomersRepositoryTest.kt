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

import com.mastrosql.app.data.customers.NetworkCustomersMasterDataRepository
import com.mastrosql.app.fake.FakeDataSource
import com.mastrosql.app.fake.FakeMastroAndroidApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkMarsRepositoryTest {

    @Test
    fun networkCustomersMasterDataRepository_getCustomersMasterDataResponse_verifyCustomersMasterDataResponseList() =
        runTest {
            val repository = NetworkCustomersMasterDataRepository(
                mastroAndroidApiService = FakeMastroAndroidApiService()
            )
            assertEquals(
                FakeDataSource.customersMasterDataResponse,
                repository.getCustomersMasterData()
            )
        }
}
