package com.mastrosql.app

import android.app.Application
import com.mastrosql.app.data.AppContainer
import com.mastrosql.app.data.DefaultAppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
//MastroAndroidApplication is the entry point for the app. It initializes the dependency graph and sets up the app-level components.
class MastroAndroidApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     * */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}