package com.mastrosql.app.data.login

import com.google.gson.JsonObject
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import retrofit2.Response

/**
 * Repository interface for handling login operations.
 *
 * This interface provides methods for logging in, logging out, checking login status,
 * completing login, and updating the API service used for login-related operations.
 */
interface LoginRepository {

    /**
     * Performs the login operation using the provided username and password.
     */
    suspend fun login(username: String, password: String): Response<JsonObject>

    /**
     * Performs the logout operation for the current user session.
     */
    suspend fun logout(): Response<JsonObject>


    /**
     * Retrieves the current login status of the user.
     */
    suspend fun getLoginStatus(): Response<JsonObject>


    /**
     * Confirms that the login process has been completed successfully.
     */
    suspend fun loginCompleted(): Response<JsonObject>

    /**
     * Updates the instance of [MastroAndroidApiService] used for network operations.
     *
     * This function allows for changing the current instance of [MastroAndroidApiService]
     * to a new one, which might be necessary when the base URL or other configurations change.
     */
    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService)
}

