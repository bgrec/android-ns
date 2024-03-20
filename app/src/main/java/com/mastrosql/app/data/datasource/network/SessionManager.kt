package com.mastrosql.app.data.datasource.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mastrosql.app.data.local.UserPreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionManager(private val dataStore: DataStore<Preferences>) {

    private var sessionCookie: String? = null
    companion object {
        val SESSION_KEY = stringPreferencesKey(UserPreferencesKeys.SESSION_KEY.name)
    }

    fun getSession(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[SESSION_KEY]
        }
    }

    suspend fun saveSession(session: String) {
        dataStore.edit { preferences ->
            preferences[SESSION_KEY] = session
        }
    }
}