package com.mastrosql.app.data.datasource.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mastrosql.app.data.local.UserPreferencesKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object SessionManager {

    private val SESSION_KEY = stringPreferencesKey(UserPreferencesKeys.SESSION_KEY.name)
    private var sessionCookie: String? = null
    private var dataStore: DataStore<Preferences>? = null

    fun setDataStore(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
    }

    fun setSessionCookie(cookieSession: String) {
        sessionCookie = cookieSession
        CoroutineScope(Dispatchers.IO).launch {
            saveSession(cookieSession)
        }

    }

    fun getSessionCookie(): String? {
        return sessionCookie ?: runBlocking {
            getSessionFromDataStore()
        }
    }

    private suspend fun getSessionFromDataStore(): String? {
        return dataStore?.data?.firstOrNull()?.get(SESSION_KEY)
    }

    private suspend fun saveSession(session: String) {
        dataStore?.edit { preferences ->
            preferences[SESSION_KEY] = session
        }
    }
    fun clearSession() {
        sessionCookie = null
        CoroutineScope(Dispatchers.IO).launch {
            dataStore?.edit { preferences ->
                preferences.remove(SESSION_KEY)
            }
        }
    }
}