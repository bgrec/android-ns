package com.mastrosql.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.mastrosql.app.data.AppContainer
import com.mastrosql.app.data.DefaultAppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
//MastroAndroidApplication is the entry point for the app. It initializes the dependency graph and sets up the app-level components.
class MastroAndroidApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     * */
    lateinit var appContainer: AppContainer

    /*
    override fun getWorkManagerConfiguration(): Configuration {
        return if (BuildConfig.DEBUG) {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build()
        } else {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.ERROR)
                .build()
        }
    }
    */

    override fun onCreate() {
        super.onCreate()

        // Initialize your AppContainer
        appContainer = DefaultAppContainer(this)
        //userPreferencesRepository = UserPreferencesRepository(dataStore)

        /* not used for now, modify the Manifest file to use this custom initializer
        // Initialize CMDWorkerFactory with AppContainer
        val workerFactory = WorkerFactory(appContainer)

        // Configure WorkManager with WorkerFactory
        val configuration = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

        // Initialize WorkManager
        WorkManager.initialize(this, configuration)

         */

    }
}

class HiltApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }
}
