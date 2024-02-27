package com.mastrosql.app.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mastrosql.app.ui.navigation.main.MainNavOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class UserPreferences(
    val searchValue: String = "",
    val isLinearLayout: Boolean = true,
    val isOnboarded: Boolean = false,
    val isLoggedIn: Boolean = false,
    val baseUrl: String = "",
    val activeButtons: java.util.HashMap<MainNavOption, Boolean> = hashMapOf(),
    val username: String = "",
    val userType: String = "",
    val userErpCode: Comparable<*> = 0,
)

/**
 * Class that handles saving and retrieving user preferences
 */
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val tag = "MastroAndroidUserPref."

    private val gson = Gson()

    private object UserPreferencesKeys {
        val SEARCH_VALUE = stringPreferencesKey("search_value")
        val IS_LINEAR_LAYOUT = booleanPreferencesKey("is_linear_layout")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val BASE_URL = stringPreferencesKey("base_url")
        val ACTIVE_BUTTONS = stringPreferencesKey("active_buttons")
        val USERNAME = stringPreferencesKey("username")
        val USER_TYPE = stringPreferencesKey("user_type")
        val USER_ERP_CODE = stringPreferencesKey("user_erp_code")
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
        val activeButtons =
            preferences[UserPreferencesKeys.ACTIVE_BUTTONS]?.toHashMap() ?: hashMapOf()
        val username = preferences[UserPreferencesKeys.USERNAME] ?: ""
        val userType = preferences[UserPreferencesKeys.USER_TYPE] ?: ""
        val userErpCode = preferences[UserPreferencesKeys.USER_ERP_CODE] ?: 0

        return UserPreferences(
            searchValue,
            isLinearLayout,
            isOnboarded,
            isLoggedIn,
            baseUrl,
            activeButtons,
            username,
            userType,
            userErpCode
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
    }

    suspend fun saveActiveButtons(activeButtons: HashMap<MainNavOption, Boolean>) {
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

    // Function to serialize a HashMap to a JSON string
    private fun HashMap<MainNavOption, Boolean>.toJsonString(): String {
        return gson.toJson(this)
    }

    // Function to deserialize a JSON string to a HashMap
    private fun String.toHashMap(): HashMap<MainNavOption, Boolean> {
        return gson.fromJson(this, object : TypeToken<HashMap<MainNavOption, Boolean>>() {}.type)
    }

    fun getActiveButtons(): Flow<HashMap<MainNavOption, Boolean>> {
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

    private fun handleError(tag: String, exception: Throwable) {
        if (exception is IOException) {
            Log.e(tag, "Error reading preferences", exception)
        } else {
            Log.e(tag, "Error reading preferences for key: Unknown", exception)
            throw exception
        }
    }
}


/*
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

***
   /* Not used
   suspend fun saveHashMapPreferences(hashMap: HashMap<MainNavOption, Boolean>) {
       val jsonString = hashMap.toJsonString()
       dataStore.edit { preferences ->
           preferences[UserPreferencesKeys.ACTIVE_BUTTONS] = jsonString
       }
   }

   suspend fun getHashMapPreferences(): HashMap<MainNavOption, Boolean> {
       val jsonString = dataStore.data.firstOrNull()?.get(UserPreferencesKeys.ACTIVE_BUTTONS) ?: ""
       return jsonString.toHashMap()
   }


   // Function to deserialize a JSON string to a HashMap
   private inline fun <reified K, reified V> String.toHashMap(): HashMap<K, V> {
       return gson.fromJson(this, object : TypeToken<HashMap<K, V>>() {}.type)
   }

   // Function to save a HashMap to user preferences
   private suspend inline fun <reified K, reified V> saveHashMapPreferences(
       key: String,
       hashMap: HashMap<K, V>
   ) {
       val jsonString = hashMap.toJsonString<K, V>()
       dataStore.edit { preferences ->
           preferences[stringPreferencesKey(key)] = jsonString
       }
   }

   // Function to retrieve a HashMap from user preferences
   private suspend inline fun <reified K, reified V> getHashMapPreferences(key: String): HashMap<K, V> {
       val jsonString = dataStore.data.firstOrNull()?.get(stringPreferencesKey(key)) ?: ""
       return jsonString.toHashMap<K, V>()
   }

    */
***

/*private companion object {
    val IS_LINEAR_LAYOUT = booleanPreferencesKey("is_linear_layout")
    val SEARCH_VALUE = stringPreferencesKey("search_value")
    val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    const val TAG = "UserPreferencesRepo"
}*/
private companion object {
    const val TAG = "UserPreferencesRepo"
}

val isLinearLayout: Flow<Boolean> = getValueFlow(UserPreferencesKeys.IS_LINEAR_LAYOUT, true)

val isOnboardingCompleted: Flow<Boolean> = getValueFlow(UserPreferencesKeys.IS_ONBOARDED, false)

val isLoggedIn: Flow<Boolean> = getValueFlow(UserPreferencesKeys.IS_LOGGED_IN, false)

val baseUrl: Flow<String> = getValueFlow(UserPreferencesKeys.BASE_URL, "")


private inline fun <reified T : Any> getValueFlow(
    key: Preferences.Key<T>,
    defaultValue: T
): Flow<T> {
    return dataStore.data
        .catch { e -> handleError(e, key) }
        .map { preferences -> preferences[key] ?: defaultValue }
}

private fun <T : Any> handleError(exception: Throwable, key: Preferences.Key<T>) {
    if (exception is IOException) {
        Log.e(TAG, "Error reading preferences, key: ${key.name}", exception)
    } else {
        throw exception
    }
}

suspend fun saveLayoutPreference(isLinearLayout: Boolean) {
    savePreference(UserPreferencesKeys.IS_LINEAR_LAYOUT, isLinearLayout)
}

suspend fun saveOnBoardingCompleted(isOnboarded: Boolean) {
    savePreference(UserPreferencesKeys.IS_ONBOARDED, isOnboarded)
}

suspend fun saveLoggedIn(isLoggedIn: Boolean) {
    savePreference(UserPreferencesKeys.IS_LOGGED_IN, isLoggedIn)
}

suspend fun updateUserPreferences(searchValue: String) {
    savePreference(UserPreferencesKeys.SEARCH_VALUE, searchValue)
}

private suspend fun <T : Any> savePreference(key: Preferences.Key<T>, value: T) {
    dataStore.edit { preferences ->
        preferences[key] = value
    }
}

val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
    .catch { e -> handleError(e, UserPreferencesKeys.SEARCH_VALUE) }
    .map { preferences ->
        UserPreferences(
            searchValue = preferences[UserPreferencesKeys.SEARCH_VALUE] ?: ""
        )
    }

/*val isLinearLayout: Flow<Boolean> = dataStore.data
    .catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }
    .map { preferences ->
        preferences[IS_LINEAR_LAYOUT] ?: true
    }*/

/*val isOnboardingCompleted: Flow<Boolean> = dataStore.data
    .catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading preferences, key: is_onboarded", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }
    .map { preferences ->
        preferences[IS_ONBOARDED] ?: false
    }

val isLoggedIn: Flow<Boolean> = dataStore.data
    .catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading preferences, key: is_logged_in", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }
    .map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }*/
/*
suspend fun saveLayoutPreference(isLinearLayout: Boolean) {
    dataStore.edit { preferences ->
        preferences[IS_LINEAR_LAYOUT] = isLinearLayout
    }
}

suspend fun saveOnBoardingCompleted(isOnboarded: Boolean) {
    dataStore.edit { preferences ->
        preferences[IS_ONBOARDED] = isOnboarded
    }
}

suspend fun saveLoggedIn(isLoggedIn: Boolean) {
    dataStore.edit { preferences ->
        preferences[IS_LOGGED_IN] = isLoggedIn
    }
}

// Update the user preferences
suspend fun updateUserPreferences(
    searchValue: String,
) {
    dataStore.edit { preferences ->
        preferences[SEARCH_VALUE] = searchValue
    }
}

// Read the user preferences as a Flow
val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
    .catch { exception ->
        if (exception is IOException) {
            // Handle the exception
        } else {
            throw exception
        }
    }
    .map { preferences ->
        UserPreferences(
            searchValue = preferences[SEARCH_VALUE] ?: "",
        )
    }*/
}
*/