package com.mastrosql.app.data.login

import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import retrofit2.Response
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Repository implementation for handling user login, logout, and session management.
 *
 * This class provides methods for user authentication and managing login sessions using the
 * [MastroAndroidApiService]. It also allows updating the instance of [MastroAndroidApiService].
 */
class NetworkLoginRepository(
    private var mastroAndroidApiService: MastroAndroidApiService,
) : LoginRepository {

    /**
     * Logs in a user with the given username and password.
     *
     * This function encodes the username and password using Base64 encoding and sends a login
     * request to the server.
     */
    @ExperimentalEncodingApi
    override suspend fun login(username: String, password: String): Response<JsonObject> {
        val authenticationAppName = "MySQL Internal"
        val base64String =
            "Basic " + Base64.encode("$username:$password".toByteArray())

        return mastroAndroidApiService.login(authenticationAppName, base64String)
    }

    /**
     * Logs out the current user.
     *
     * This function sends a logout request to the server using the `mastroAndroidApiService`.
     */
    override suspend fun logout(): Response<JsonObject> {
        return mastroAndroidApiService.logout()
    }

    /**
     * Retrieves the current login status from the server.
     *
     * This function sends a request to the server to get the current login status using the `mastroAndroidApiService`.
     */
    override suspend fun getLoginStatus(): Response<JsonObject> {
        return mastroAndroidApiService.getLoginStatus()
    }

    /**
     * Notifies the server that the login process has been completed.
     *
     * This function sends a request to the server to indicate that the login process
     * has been completed using the `mastroAndroidApiService`.
     */
    override suspend fun loginCompleted(): Response<JsonObject> {
        return mastroAndroidApiService.getLoginCompleted()
    }

    /**
     * Updates the [MastroAndroidApiService] instance used for network operations.
     *
     * This method updates the [mastroAndroidApiService] instance to the provided
     * [newMastroAndroidApiService], allowing the repository to communicate with
     * the backend using the updated service.
     */
    override fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }
}

