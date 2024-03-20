package com.mastrosql.app.data.datasource.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mastrosql.app.data.local.UserPreferencesKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

data class Session(val sessionId: String, val expirationTime: Long)

object AppCookieJar : CookieJar {


    private val SESSION_KEY = stringPreferencesKey(UserPreferencesKeys.SESSION_KEY.name)
    private var sessionCookie: String? = null
    private var dataStore: DataStore<Preferences>? = null

    fun setDataStore(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
    }
    fun setSessionCookie(cookie: String) {
        sessionCookie = cookie
        CoroutineScope(Dispatchers.IO).launch {
            saveSession(cookie)
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

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        // Handle saving cookies here if needed
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = mutableListOf<Cookie>()
        sessionCookie?.let {
            val cookie = Cookie.parse(url, it)
            cookie?.let {
                cookies.add(it)
            }
        }
        return cookies
    }

    fun getSession(): Flow<String?> {
        return dataStore?.data?.map { preferences ->
            preferences[SESSION_KEY]
        } ?: throw IllegalStateException("DataStore not initialized")
    }

    private suspend fun saveSession(session: String) {
        dataStore?.edit { preferences ->
            preferences[SESSION_KEY] = session
        }
    }
}