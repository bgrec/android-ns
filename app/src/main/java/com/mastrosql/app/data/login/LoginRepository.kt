package com.mastrosql.app.data.login

import com.google.gson.JsonObject
import retrofit2.Response

interface LoginRepository {
    suspend fun login(username: String, password: String): Response<JsonObject>
    suspend fun logout(): Response<JsonObject>

    suspend fun getLoginStatus(): Response<JsonObject>

    suspend fun loginCompleted(): Response<JsonObject>
}

