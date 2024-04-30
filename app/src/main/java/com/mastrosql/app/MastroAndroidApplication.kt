package com.mastrosql.app

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.mastrosql.app.data.AppContainer
import com.mastrosql.app.data.DefaultAppContainer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@HiltAndroidApp
//MastroAndroidApplication is the entry point for the app.
// It initializes the dependency graph and sets up the app-level components.

class MastroAndroidApplication : Application(), CoroutineScope {

    // CoroutineScope implementation to handle coroutines in the app lifecycle
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob()


    //AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var appContainer: AppContainer

    // Constants for the polling interval
    private val pollingInterval = 300_000L // 300 seconds

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

        // Initialize the AppContainer
        appContainer = DefaultAppContainer(this)

        //userPreferencesRepository = UserPreferencesRepository(dataStore)

        // Start API polling when the app starts to maintain the user's session
        // active in the server
        startPolling()

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

    // Function to start polling the API to maintain the user's session active
    private fun startPolling() {
        launch {
            while (true) {
                try {
                    val response = appContainer.mastroAndroidApiService.getLoginStatus()
                    if (!response.isSuccessful) {
                        Log.e("Polling", "Error polling the API")
                    }
                    // TODO Remove this log statement in production
                    Log.d("Polling", "Response: $response")

                } catch (e: Exception) {
                    Log.e("Polling", "Error polling the API: ${e.message}")
                }
                delay(pollingInterval)
            }
        }
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
