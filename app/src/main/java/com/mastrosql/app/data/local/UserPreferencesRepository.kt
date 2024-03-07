package com.mastrosql.app.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
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

data class UserPreferences(
    val searchValue: String = "",
    val isLinearLayout: Boolean = true,
    val isOnboarded: Boolean = false,
    val isLoggedIn: Boolean = false,
    val baseUrl: String = "",
    val activeButtons: EnumMap<MainNavOption, Boolean> = EnumMap(MainNavOption::class.java),
    val username: String = "",
    val userType: String = "",
    val userErpCode: Comparable<*> = 0,
    val appVersion: String = ""
)

object UserPreferencesKeys {
    val SEARCH_VALUE = stringPreferencesKey("search_value")
    val IS_LINEAR_LAYOUT = booleanPreferencesKey("is_linear_layout")
    val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val BASE_URL = stringPreferencesKey("base_url")
    val ACTIVE_BUTTONS = stringPreferencesKey("active_buttons")
    val USERNAME = stringPreferencesKey("username")
    val USER_TYPE = stringPreferencesKey("user_type")
    val USER_ERP_CODE = stringPreferencesKey("user_erp_code")
    val APP_VERSION = stringPreferencesKey("app_version")
}

/**
 * Class that handles saving and retrieving user preferences
 */
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val appContainer: AppContainer,
    private var mastroAndroidApiService: MastroAndroidApiService
) {
    private val tag = "MastroAndroidUserPref."
    private val gson = Gson()


    fun updateMastroAndroidApiService(newMastroAndroidApiService: MastroAndroidApiService) {
        this.mastroAndroidApiService = newMastroAndroidApiService
    }

    /**
     * Get the user preferences flow.
     */
    private val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            Log.e(tag, "Error reading preferences.", exception)
            emit(emptyPreferences())
        } else {
            handleError(tag, exception)
        }
        handleError(tag, exception)
    }.map { preferences ->
        mapUserPreferences(preferences)
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {

        val searchValue = preferences[UserPreferencesKeys.SEARCH_VALUE] ?: ""
        val isLinearLayout = preferences[UserPreferencesKeys.IS_LINEAR_LAYOUT] ?: true
        val isOnboarded = preferences[UserPreferencesKeys.IS_ONBOARDED] ?: false
        val isLoggedIn = preferences[UserPreferencesKeys.IS_LOGGED_IN] ?: false
        val baseUrl = preferences[UserPreferencesKeys.BASE_URL] ?: ""
        val activeButtonsJson = preferences[UserPreferencesKeys.ACTIVE_BUTTONS]
        val activeButtons = if (activeButtonsJson != null) {
            try {
                activeButtonsJson.toEnumMap()
            } catch (e: Exception) {
                // Handle enum mismatch or JSON parsing error
                Log.e("UserPreferences", "Error parsing activeButtons JSON", e)
                EnumMap(MainNavOption::class.java) // Provide fallback value
            }
        } else {
            EnumMap(MainNavOption::class.java) // Provide default value if preferences are empty
        }
        val username = preferences[UserPreferencesKeys.USERNAME] ?: ""
        val userType = preferences[UserPreferencesKeys.USER_TYPE] ?: ""
        val userErpCode = preferences[UserPreferencesKeys.USER_ERP_CODE] ?: 0
        val appVersion = preferences[UserPreferencesKeys.APP_VERSION] ?: ""

        return UserPreferences(
            searchValue,
            isLinearLayout,
            isOnboarded,
            isLoggedIn,
            baseUrl,
            activeButtons,
            username,
            userType,
            userErpCode,
            appVersion
        )
    }

    suspend fun updateUserPreferences(userPreferences: UserPreferences) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SEARCH_VALUE] = userPreferences.searchValue
            preferences[UserPreferencesKeys.IS_LINEAR_LAYOUT] = userPreferences.isLinearLayout
            preferences[UserPreferencesKeys.IS_ONBOARDED] = userPreferences.isOnboarded
            preferences[UserPreferencesKeys.IS_LOGGED_IN] = userPreferences.isLoggedIn
            preferences[UserPreferencesKeys.BASE_URL] = userPreferences.baseUrl
            preferences[UserPreferencesKeys.ACTIVE_BUTTONS] =
                userPreferences.activeButtons.toJsonString()
            preferences[UserPreferencesKeys.USERNAME] = userPreferences.username
            preferences[UserPreferencesKeys.USER_TYPE] = userPreferences.userType
            preferences[UserPreferencesKeys.USER_ERP_CODE] = userPreferences.userErpCode.toString()
            preferences[UserPreferencesKeys.APP_VERSION] = userPreferences.appVersion

        }
    }

    suspend fun saveLayoutPreference(isLinearLayout: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_LINEAR_LAYOUT] = isLinearLayout
        }
    }

    suspend fun saveOnBoardingCompleted(isOnboarded: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_ONBOARDED] = isOnboarded
        }
    }

    suspend fun saveLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun saveBaseUrl(baseUrl: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.BASE_URL] = baseUrl
        }
        //Create a new Retrofit instance with the new base URL
        appContainer.updateRetrofitService(baseUrl)
    }

    suspend fun saveActiveButtons(activeButtons: EnumMap<MainNavOption, Boolean>) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.ACTIVE_BUTTONS] = activeButtons.toJsonString()
        }
    }

    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USERNAME] = username
        }
    }

    suspend fun saveUserType(userType: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USER_TYPE] = userType
        }
    }

    suspend fun saveUserErpCode(userErpCode: Comparable<*>) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USER_ERP_CODE] = userErpCode.toString()
        }
    }

    suspend fun fetchInitialPreferences() =
        mapUserPreferences(dataStore.data.first().toPreferences())

    // Function to serialize a EnumMap to a JSON string
    private fun EnumMap<MainNavOption, Boolean>.toJsonString(): String {
        return gson.toJson(this)
    }

    // Function to deserialize a JSON string to a EnumMap
    private fun String.toEnumMap(): EnumMap<MainNavOption, Boolean> {
        //return gson.fromJson(this, object : TypeToken<EnumMap<MainNavOption, Boolean>>() {}.type)
        val mapType = object : TypeToken<HashMap<String, Boolean>>() {}.type
        val gson = Gson()
        val map: HashMap<String, Boolean> = gson.fromJson(this, mapType)

        // Convert HashMap<String, Boolean> to EnumMap<MainNavOption, Boolean>
        val enumMap: EnumMap<MainNavOption, Boolean> = EnumMap(MainNavOption::class.java)

        for ((key, value) in map) {
            val option = MainNavOption.valueOf(key)
            enumMap[option] = value
        }
        return enumMap
    }

    fun getActiveButtons(): Flow<EnumMap<MainNavOption, Boolean>> {
        return userPreferencesFlow.map { it.activeButtons }
    }

    fun getBaseUrl(): Flow<String> {
        return userPreferencesFlow.map { it.baseUrl }
    }

    fun getIsLinearLayout(): Flow<Boolean> {
        return userPreferencesFlow.map { it.isLinearLayout }
    }

    fun getIsOnboarded(): Flow<Boolean> {
        return userPreferencesFlow.map { it.isOnboarded }
    }

    fun getIsLoggedIn(): Flow<Boolean> {
        return userPreferencesFlow.map { it.isLoggedIn }
    }

    fun getUsername(): Flow<String> {
        return userPreferencesFlow.map { it.username }
    }

    fun getUserType(): Flow<String> {
        return userPreferencesFlow.map { it.userType }
    }

    fun getUserErpCode(): Flow<Comparable<*>> {
        return userPreferencesFlow.map { it.userErpCode }
    }

    fun getSearchValue(): Flow<String> {
        return userPreferencesFlow.map { it.searchValue }
    }

    fun getAppVersion(): Flow<String> {
        return userPreferencesFlow.map { it.appVersion }
    }

    suspend fun saveAppVersion(appVersion: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.APP_VERSION] = appVersion
        }
    }

    suspend fun updateActiveButtons(activeButtons: EnumMap<MainNavOption, Boolean>) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.ACTIVE_BUTTONS] = activeButtons.toJsonString()
        }
    }

    private fun handleError(tag: String, exception: Throwable) {
        if (exception is IOException) {
            Log.e(tag, "Error reading preferences", exception)
        } else {
            Log.e(tag, "Error reading preferences for key: Unknown", exception)
            throw exception
        }
    }

    suspend fun testApiCall(): Response<JsonObject> {
        return mastroAndroidApiService.testApiCall()
    }
}
