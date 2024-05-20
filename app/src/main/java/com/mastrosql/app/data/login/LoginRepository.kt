package com.mastrosql.app.data.login

import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import retrofit2.Response

interface LoginRepository {
    suspend fun login(username: String, password: String): Response<JsonObject>
    suspend fun logout(): Response<JsonObject>

    suspend fun getLoginStatus(): Response<JsonObject>

    suspend fun loginCompleted(): Response<JsonObject>

    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService)
}

