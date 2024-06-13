package com.mastrosql.app.data.login

import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import retrofit2.Response
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class NetworkLoginRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
) : LoginRepository {

    @ExperimentalEncodingApi
    override suspend fun login(username: String, password: String): Response<JsonObject> {
        val authenticationAppName = "MySQL Internal"
        val base64String =
            "Basic " + Base64.encode("$username:$password".toByteArray())

        return mastroAndroidApiService.login(authenticationAppName, base64String)
    }

    override suspend fun logout(): Response<JsonObject> {
        return mastroAndroidApiService.logout()
    }

    override suspend fun getLoginStatus(): Response<JsonObject> {
        return mastroAndroidApiService.getLoginStatus()
    }

    override suspend fun loginCompleted(): Response<JsonObject> {
        return mastroAndroidApiService.getLoginCompleted()
    }

    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }
}

