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
package com.mastrosql.app.fake

import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesResponse
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataResponse
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationsDataResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrderAddResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersResponse
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsResponse
import retrofit2.Response

class FakeMastroAndroidApiService : MastroAndroidApiService {

    override suspend fun getAllCustomersMasterData(
        offset: Int, pageSize: Int
    ): CustomersMasterDataResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomersMasterDataPage(
        offset: Int, pageSize: Int
    ): CustomersMasterDataResponse {
        TODO("Not yet implemented")
    }

    override suspend fun testApiCall(offset: Int, pageSize: Int): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllDestinationsData(
        offset: Int, pageSize: Int
    ): DestinationsDataResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getAllArticles(offset: Int, pageSize: Int): ArticlesResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getAllOrders(offset: Int, pageSize: Int): OrdersResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getOrderByFilter(filter: String): OrdersResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getOrderDetails(filter: String): OrderDetailsResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getAllOrderDetails(offset: Int, pageSize: Int): OrderDetailsResponse {
        TODO("Not yet implemented")
    }

    override suspend fun sendOrderScannedCode(body: JsonObject): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOrderDetailItem(filter: String): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun insertArticleIntoDocument(body: JsonObject): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun duplicateOrderDetailItem(body: JsonObject): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrderDetailItem(body: JsonObject): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun insertNewOrder(body: JsonObject): Response<OrderAddResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrderDeliveryState(body: JsonObject): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrder(body: JsonObject): Response<OrderAddResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun login(appName: String, authorization: String): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun getLoginStatus(): Response<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun getLoginCompleted(): Response<JsonObject> {
        TODO("Not yet implemented")
    }

}
