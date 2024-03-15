package com.mastrosql.app.data.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mastrosql.app.data.AppContainer
import com.mastrosql.app.data.datasource.network.MastroAndroidApiService
import javax.inject.Inject

class LoginRepository (
    private var mastroAndroidApiService: MastroAndroidApiService) {
}

