package com.mastrosql.app.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.mastrosql.app.PRIMARY_URL
import com.mastrosql.app.SECONDARY_URL
import com.mastrosql.app.data.AppContainer
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import com.mastrosql.app.ui.navigation.main.MainNavOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException
import java.util.EnumMap
import javax.inject.Inject

/**
 * Data class to hold user preferences
 */
@Suppress("KDocMissingDocumentation")
data class UserPreferences(
    val searchValue: String = "",
    val isLinearLayout: Boolean = true,
    val isOnboarded: Boolean = false,
    val isLoggedIn: Boolean = false,
    val baseUrl: String = "",
    val baseUrl2: String = "",
    val selectedUrl: Int = PRIMARY_URL,
    val baseUrlName: String = "Primary",
    val baseUrl2Name: String = "Secondary",
    val activeButtons: EnumMap<MainNavOption, Boolean> = EnumMap(MainNavOption::class.java),
    val username: String = "",
    val userType: String = "",
    val userErpCode: Comparable<*> = 0,
    val appVersion: String = "",
    val sessionToken: String = "",
    val isNotSecuredApi: Boolean = false,
    val isSwipeToDeleteDisabled: Boolean = false,
    val isSwipeToDuplicateDisabled: Boolean = false
)

/**
 * Data class to hold user preferences keys
 */
@Suppress("KDocMissingDocumentation")
object UserPreferencesKeys {
    val SEARCH_VALUE: Preferences.Key<String> = stringPreferencesKey("search_value")
    val IS_LINEAR_LAYOUT: Preferences.Key<Boolean> = booleanPreferencesKey("is_linear_layout")
    val IS_ONBOARDED: Preferences.Key<Boolean> = booleanPreferencesKey("is_onboarded")
    val IS_LOGGED_IN: Preferences.Key<Boolean> = booleanPreferencesKey("is_logged_in")
    val BASE_URL: Preferences.Key<String> = stringPreferencesKey("base_url")
    val BASE_URL2: Preferences.Key<String> = stringPreferencesKey("base_url2")
    val SELECTED_URL: Preferences.Key<Int> = intPreferencesKey("selected_url")
    val BASE_URL_NAME: Preferences.Key<String> = stringPreferencesKey("base_url_name")
    val BASE_URL2_NAME: Preferences.Key<String> = stringPreferencesKey("base_url2_name")
    val ACTIVE_BUTTONS: Preferences.Key<String> = stringPreferencesKey("active_buttons")
    val USERNAME: Preferences.Key<String> = stringPreferencesKey("username")
    val USER_TYPE: Preferences.Key<String> = stringPreferencesKey("user_type")
    val USER_ERP_CODE: Preferences.Key<String> = stringPreferencesKey("user_erp_code")
    val APP_VERSION: Preferences.Key<String> = stringPreferencesKey("app_version")
    val SESSION_KEY: Preferences.Key<String> = stringPreferencesKey("session_key")
    val IS_NOT_SECURED_API: Preferences.Key<Boolean> = booleanPreferencesKey("is_not_secured_api")
    val IS_SWIPE_TO_DELETE_DISABLED: Preferences.Key<Boolean> =
        booleanPreferencesKey("is_swipe_to_delete_disabled")
    val IS_SWIPE_TO_DUPLICATE_DISABLED: Preferences.Key<Boolean> =
        booleanPreferencesKey("is_swipe_to_duplicate_disabled")
}

/**
 * Repository class to handle user preferences
 */
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val appContainer: AppContainer,
    private var mastroAndroidApiService: MastroAndroidApiService
) {
    private val tag = "MastroAndroidUserPref."
    private val gson = Gson()

    /**
     * Update the MastroAndroidApiService instance after changing the base URL
     */
    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    /**
     * Get the user preferences flow.
     */
    private val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->

            /**
             * Handles exceptions that occur when reading preferences.
             * If the [exception] is an instance of [IOException],
             * logs an error message with the specified [tag] and emits empty preferences.
             */
            if (exception is IOException) {
                Log.e(tag, "Error reading preferences.", exception)
                emit(emptyPreferences())
            }

            /**
             * Handles exceptions that occur during preferences reading.
             * If the [exception] is an instance of [IOException],
             * logs an error message with the specified [tag] and emits empty preferences.
             * Otherwise, invokes [handleError]
             * to handle the exception.
             *
             * After handling the exception or successful preferences retrieval,
             * maps the retrieved raw [UserPreferences] to user-specific
             * preferences using [mapUserPreferences].
             */
            else {
                Log.e(tag, "Error reading preferences for key: Unknown", exception)
                handleError(tag, exception)
            }
            Log.e(tag, "Error reading preferences for key: Unknown", exception)
            handleError(tag, exception)
        }
        .map { preferences ->
            mapUserPreferences(preferences)
        }

    /**
     * Maps raw [preferences] data to [UserPreferences] object.
     *
     * This function extracts various user-specific preferences
     * from the provided [preferences] object
     * and constructs a [UserPreferences] instance.
     */
    private fun mapUserPreferences(preferences: Preferences): UserPreferences {

        val searchValue = preferences[UserPreferencesKeys.SEARCH_VALUE] ?: ""
        val isLinearLayout = preferences[UserPreferencesKeys.IS_LINEAR_LAYOUT] ?: true
        val isOnboarded = preferences[UserPreferencesKeys.IS_ONBOARDED] ?: false
        val isLoggedIn = preferences[UserPreferencesKeys.IS_LOGGED_IN] ?: false
        val baseUrl = preferences[UserPreferencesKeys.BASE_URL] ?: ""
        val baseUrl2 = preferences[UserPreferencesKeys.BASE_URL2] ?: ""
        val selectedUrl = preferences[UserPreferencesKeys.SELECTED_URL] ?: PRIMARY_URL
        val baseUrlName = preferences[UserPreferencesKeys.BASE_URL_NAME] ?: "Primary"
        val baseUrl2Name = preferences[UserPreferencesKeys.BASE_URL2_NAME] ?: "Secondary"
        val activeButtonsJson = preferences[UserPreferencesKeys.ACTIVE_BUTTONS]

        //Active buttons EnumMap
        val activeButtons = if (activeButtonsJson != null) {
            /**
             * Converts a JSON string representing enum key-value pairs into an EnumMap.
             */
            try {
                activeButtonsJson.toEnumMap()
            }

            /**
             * Handles exceptions encountered while parsing JSON
             * representing active buttons into an EnumMap.
             * Logs the error and provides a fallback EnumMap of type [MainNavOption].
             */
            catch (e: Exception) {
                // Handle enum mismatch or JSON parsing error
                Log.e("UserPreferences", "Error parsing activeButtons JSON", e)
                EnumMap(MainNavOption::class.java) // Provide fallback value
            }
        }

        /**
         * Provides a default EnumMap of [MainNavOption] when preferences are empty.
         */
        else {
            EnumMap(MainNavOption::class.java) // Provide default value if preferences are empty
        }
        //End of active buttons EnumMap

        val username = preferences[UserPreferencesKeys.USERNAME] ?: ""
        val userType = preferences[UserPreferencesKeys.USER_TYPE] ?: ""
        val userErpCode = preferences[UserPreferencesKeys.USER_ERP_CODE] ?: 0
        val appVersion = preferences[UserPreferencesKeys.APP_VERSION] ?: ""
        val sessionToken = preferences[UserPreferencesKeys.SESSION_KEY] ?: ""
        val isNotSecuredApi = preferences[UserPreferencesKeys.IS_NOT_SECURED_API] ?: false
        val isSwipeToDeleteDisabled =
            preferences[UserPreferencesKeys.IS_SWIPE_TO_DELETE_DISABLED] ?: false
        val isSwipeToDuplicateDisabled =
            preferences[UserPreferencesKeys.IS_SWIPE_TO_DUPLICATE_DISABLED] ?: false

        return UserPreferences(
            searchValue,
            isLinearLayout,
            isOnboarded,
            isLoggedIn,
            baseUrl,
            baseUrl2,
            selectedUrl,
            baseUrlName,
            baseUrl2Name,
            activeButtons,
            username,
            userType,
            userErpCode,
            appVersion,
            sessionToken,
            isNotSecuredApi,
            isSwipeToDeleteDisabled,
            isSwipeToDuplicateDisabled
        )
    }

    /**
     * Update the user preferences.
     */
    suspend fun updateUserPreferences(userPreferences: UserPreferences) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SEARCH_VALUE] = userPreferences.searchValue
            preferences[UserPreferencesKeys.IS_LINEAR_LAYOUT] = userPreferences.isLinearLayout
            preferences[UserPreferencesKeys.IS_ONBOARDED] = userPreferences.isOnboarded
            preferences[UserPreferencesKeys.IS_LOGGED_IN] = userPreferences.isLoggedIn
            preferences[UserPreferencesKeys.BASE_URL] = userPreferences.baseUrl
            preferences[UserPreferencesKeys.BASE_URL2] = userPreferences.baseUrl2
            preferences[UserPreferencesKeys.SELECTED_URL] = userPreferences.selectedUrl
            preferences[UserPreferencesKeys.BASE_URL_NAME] = userPreferences.baseUrlName
            preferences[UserPreferencesKeys.BASE_URL2_NAME] = userPreferences.baseUrl2Name
            preferences[UserPreferencesKeys.ACTIVE_BUTTONS] =
                userPreferences.activeButtons.toJsonString()
            preferences[UserPreferencesKeys.USERNAME] = userPreferences.username
            preferences[UserPreferencesKeys.USER_TYPE] = userPreferences.userType
            preferences[UserPreferencesKeys.USER_ERP_CODE] = userPreferences.userErpCode.toString()
            preferences[UserPreferencesKeys.APP_VERSION] = userPreferences.appVersion
            preferences[UserPreferencesKeys.SESSION_KEY] = userPreferences.sessionToken
            preferences[UserPreferencesKeys.IS_NOT_SECURED_API] = userPreferences.isNotSecuredApi
            preferences[UserPreferencesKeys.IS_SWIPE_TO_DELETE_DISABLED] =
                userPreferences.isSwipeToDeleteDisabled
            preferences[UserPreferencesKeys.IS_SWIPE_TO_DUPLICATE_DISABLED] =
                userPreferences.isSwipeToDuplicateDisabled
        }
    }

    /**
     * Save the isOnboarded status.
     */
    suspend fun saveOnBoardingCompleted(isOnboarded: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_ONBOARDED] = isOnboarded
        }
    }

    /**
     * Saves the user's logged-in status to DataStore.
     */
    suspend fun saveLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    /**
     * Saves the base URL used for network requests to DataStore
     * and updates the Retrofit service instance.
     */
    suspend fun savePrimaryBaseUrl(baseUrl: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.BASE_URL] = baseUrl
        }
        //Create a new Retrofit instance with the new base URL
        appContainer.updateRetrofitService(baseUrl)
    }

    /**
     * Saves the secondary base URL used for network requests to DataStore
     * and updates the Retrofit service instance.
     */
    suspend fun saveSecondaryBaseUrl(baseUrl2: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.BASE_URL2] = baseUrl2
        }
        // No need to update the Retrofit service instance here
        //Create a new Retrofit instance with the new base URL
        //appContainer.updateRetrofitService(baseUrl2)
    }

    /**
     * Saves the selected URL to DataStore.
     */
    suspend fun saveSelectedUrl(selectedUrl: Int) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SELECTED_URL] = selectedUrl
        }
    }

    /**
     * Saves the base URL name to DataStore.
     */
    suspend fun saveBaseUrlName(baseUrlName: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.BASE_URL_NAME] = baseUrlName
        }
    }

    /**
     * Saves the base URL2 name to DataStore.
     */
    suspend fun saveBaseUrl2Name(baseUrl2Name: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.BASE_URL2_NAME] = baseUrl2Name
        }
    }

    /**
     * Saves the active buttons configuration to DataStore.
     */
    suspend fun saveActiveButtons(activeButtons: EnumMap<MainNavOption, Boolean>) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.ACTIVE_BUTTONS] = activeButtons.toJsonString()
        }
    }

    /**
     * Saves the username to DataStore preferences.
     */
    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USERNAME] = username
        }
    }

    /**
     * Saves the username to DataStore preferences.
     */
    suspend fun saveUserType(userType: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USER_TYPE] = userType
        }
    }

    /**
     * Saves the user ERP code to DataStore preferences.
     */
    suspend fun saveUserErpCode(userErpCode: Comparable<*>) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USER_ERP_CODE] = userErpCode.toString()
        }
    }

    /**
     * Fetches the initial user preferences from DataStore and maps them to [UserPreferences].
     * This function retrieves the preferences synchronously as a first element of the flow.
     */
    suspend fun fetchInitialPreferences() = mapUserPreferences(
        dataStore.data
            .first()
            .toPreferences()
    )

    /**
     * Converts this EnumMap to its JSON representation using Gson.
     */
    // Function to serialize a EnumMap to a JSON string
    private fun EnumMap<MainNavOption, Boolean>.toJsonString(): String {
        return gson.toJson(this)
    }

    /**
     * Converts this JSON string to an EnumMap<MainNavOption, Boolean>.
     */
    // Function to deserialize a JSON string to a EnumMap
    private fun String.toEnumMap(): EnumMap<MainNavOption, Boolean> {
        //return gson.fromJson(this, object : TypeToken<EnumMap<MainNavOption, Boolean>>() {}.type)
        val mapType = object : TypeToken<HashMap<String, Boolean>>() {}.type
        val gson = Gson()
        val map: HashMap<String, Boolean> = gson.fromJson(this, mapType)

        // Convert HashMap<String, Boolean> to EnumMap<MainNavOption, Boolean>
        val enumMap: EnumMap<MainNavOption, Boolean> = EnumMap(MainNavOption::class.java)

        /**
         * Converts this JSON string to an EnumMap<MainNavOption, Boolean>.
         */
        for ((key, value) in map) {
            val option = MainNavOption.valueOf(key)
            enumMap[option] = value
        }
        return enumMap
    }

    /**
     * Retrieves a Flow of EnumMap<MainNavOption, Boolean> representing the active buttons preferences.
     */
    fun getActiveButtons(): Flow<EnumMap<MainNavOption, Boolean>> {
        return userPreferencesFlow.map { it.activeButtons }
    }

    /**
     * Retrieves a Flow of String representing the base URL preference.
     */
    fun getBaseUrl(): Flow<String> {
        return userPreferencesFlow.map { it.baseUrl }
    }

    /**
     * Retrieves a Flow of String representing the base URL2 preference.
     */
    fun getBaseUrl2(): Flow<String> {
        return userPreferencesFlow.map { it.baseUrl2 }
    }

    /**
     * Retrieves a Flow of Int representing the selected URL preference.
     */
    fun getSelectedUrl(): Flow<Int> {
        return userPreferencesFlow.map { it.selectedUrl }
    }

    /**
     * Retrieves a Flow of String representing the base URL name preference.
     */
    fun getBaseUrlName(): Flow<String> {
        return userPreferencesFlow.map { it.baseUrlName }
    }

    /**
     * Retrieves a Flow of String representing the base URL2 name preference.
     */
    fun getBaseUrl2Name(): Flow<String> {
        return userPreferencesFlow.map { it.baseUrl2Name }
    }

    /**
     * Function to switch the base URL based on the selected URL.
     */
    suspend fun changeBaseUrl(selectedUrl: Int) {
        // Retrieve the current base URLs
        val baseUrl1 = getBaseUrl().first()
        val baseUrl2 = getBaseUrl2().first()

        // Determine the new base URL based on the selected URL
        val newBaseUrl = when (selectedUrl) {
            PRIMARY_URL -> {
                saveSelectedUrl(selectedUrl)
                baseUrl1
            }

            SECONDARY_URL -> {
                saveSelectedUrl(selectedUrl)
                baseUrl2
            }

            else -> throw IllegalArgumentException("Invalid selectedUrl: $selectedUrl")
        }

        // Update preferences and Retrofit service with the new base URL
        appContainer.updateRetrofitService(newBaseUrl)
    }

    /**
     * Retrieves a Flow of Boolean representing the onboarded status preference.
     */
    fun getIsOnboarded(): Flow<Boolean> {
        return userPreferencesFlow.map { it.isOnboarded }
    }

    /**
     * Retrieves a Flow of Boolean representing the logged-in status preference.
     */
    fun getIsLoggedIn(): Flow<Boolean> {
        return userPreferencesFlow.map { it.isLoggedIn }
    }

    /**
     * Retrieves a Flow of String representing the username preference.
     */
    fun getUsername(): Flow<String> {
        return userPreferencesFlow.map { it.username }
    }

    /**
     * Retrieves a Flow of String representing the user type preference.
     */
    fun getUserType(): Flow<String> {
        return userPreferencesFlow.map { it.userType }
    }

    /**
     * Retrieves a Flow of Comparable representing the user ERP code preference.
     */
    fun getUserErpCode(): Flow<Comparable<*>> {
        return userPreferencesFlow.map { it.userErpCode }
    }

    /**
     * Retrieves a Flow of String representing the search value preference.
     */
    fun getSearchValue(): Flow<String> {
        return userPreferencesFlow.map { it.searchValue }
    }

    /**
     * Retrieves a Flow of String representing the application version preference.
     */
    fun getAppVersion(): Flow<String> {
        return userPreferencesFlow.map { it.appVersion }
    }

    /**
     * Saves the application version to preferences asynchronously.
     */
    suspend fun saveAppVersion(appVersion: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.APP_VERSION] = appVersion
        }
    }

    /**
     * Updates the active buttons configuration in preferences asynchronously.
     */
    suspend fun updateActiveButtons(activeButtons: EnumMap<MainNavOption, Boolean>) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.ACTIVE_BUTTONS] = activeButtons.toJsonString()
        }
    }

    /**
     * Handles errors encountered while reading preferences.
     */
    private fun handleError(tag: String, exception: Throwable) {

        /**
         * Handles IOException encountered while reading preferences.
         */
        if (exception is IOException) {
            Log.e(tag, "Error reading preferences", exception)
        }

        /**
         * Handles exceptions other than IOException encountered while reading preferences.
         */
        else {
            Log.e(tag, "Error reading preferences for key: Unknown", exception)
            throw exception
        }
    }

    /**
     * Performs a test API call asynchronously.
     */
    suspend fun testApiCall(): Response<JsonObject> {
        return mastroAndroidApiService.testApiCall()
    }

    /**
     * Saves the session token to dataStore asynchronously.
     */
    suspend fun saveSessionToken(sessionToken: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SESSION_KEY] = sessionToken
        }
    }

    /**
     * Retrieves the session token as a flow from user preferences.
     */
    fun getSessionToken(): Flow<String> {
        return userPreferencesFlow.map { it.sessionToken }
    }

    /**
     * Saves the user's login status and related details to the DataStore.
     */
    suspend fun saveLoggedIn(
        isLoggedIn: Boolean,
        username: String,
        userType: String,
        userErpCode: Comparable<*>,
        sessionToken: String
    ) {
        dataStore.edit { preferences ->
            // Save login status and user details
            preferences[UserPreferencesKeys.IS_LOGGED_IN] = isLoggedIn
            preferences[UserPreferencesKeys.USERNAME] = username
            preferences[UserPreferencesKeys.USER_TYPE] = userType
            preferences[UserPreferencesKeys.USER_ERP_CODE] = userErpCode.toString()
            // Save session token
            preferences[UserPreferencesKeys.SESSION_KEY] = sessionToken
        }
    }

    /**
     * Initiates a logout request to the server using the MastroAndroidApiService.
     * This function sends a logout request and expects a response containing a JsonObject.
     */
    suspend fun logoutFromServer(): Response<JsonObject> {
        return mastroAndroidApiService.logout()
    }

    /**
     * Retrieves the flow of the 'isNotSecuredApi' boolean value from the user preferences.
     *
     * This function maps the current user preferences flow to retrieve the boolean value
     * indicating whether the API is considered not secured.
     */
    fun getIsNotSecuredApi(): Flow<Boolean> {
        return userPreferencesFlow.map { it.isNotSecuredApi }
    }

    /**
     * Suspended function to save the 'isNotSecuredApi' boolean value into user preferences.
     *
     * This function updates the user preferences with the provided boolean value indicating
     * whether the API is considered not secured.
     */
    suspend fun saveIsNotSecuredApi(isNotSecuredApi: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_NOT_SECURED_API] = isNotSecuredApi
        }
    }

    /**
     * Returns a Flow representing the 'isSwipeToDeleteDisabled' boolean value from user preferences.
     *
     * This function provides a Flow that emits the current state of 'isSwipeToDeleteDisabled' from
     * the user preferences data, allowing observers to reactively receive updates when the value changes.
     */
    fun getIsSwipeToDeleteDisabled(): Flow<Boolean> {
        return userPreferencesFlow.map { it.isSwipeToDeleteDisabled }
    }

    /**
     * Saves the 'isSwipeToDeleteDisabled' boolean flag to user preferences.
     *
     * This function updates the 'isSwipeToDeleteDisabled' flag in the user preferences data store
     * with the provided boolean value. It uses DataStore's edit block to ensure thread safety and
     * atomic updates.
     */
    suspend fun saveIsSwipeToDeleteDisabled(isSwipeToDeleteDisabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_SWIPE_TO_DELETE_DISABLED] = isSwipeToDeleteDisabled
        }
    }

    /**
     * Retrieves the 'isSwipeToDuplicateDisabled' flag as a Flow<Boolean>.
     *
     * This function retrieves the current state of the 'isSwipeToDuplicateDisabled' flag from
     * the user preferences data flow. Changes to this preference will emit updates downstream
     * through the Flow.
     */
    fun getIsSwipeToDuplicateDisabled(): Flow<Boolean> {
        return userPreferencesFlow.map { it.isSwipeToDuplicateDisabled }
    }

    /**
     * Saves the 'isSwipeToDuplicateDisabled' flag to user preferences.
     *
     * This function updates the 'isSwipeToDuplicateDisabled' flag in the data store with the provided
     * boolean value. Changes to this preference will be persisted immediately.
     */
    suspend fun saveIsSwipeToDuplicateDisabled(isSwipeToDuplicateDisabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_SWIPE_TO_DUPLICATE_DISABLED] =
                isSwipeToDuplicateDisabled
        }
    }

    /**
     * Get the swipe actions preferences.
     */
    fun getSwipeActionsPreferences(): Flow<SwipeActionsPreferences> {
        return userPreferencesFlow.map {
            SwipeActionsPreferences(
                isDeleteDisabled = getIsSwipeToDeleteDisabled().first(),
                isDuplicateDisabled = getIsSwipeToDuplicateDisabled().first()
            )
        }
    }

}
