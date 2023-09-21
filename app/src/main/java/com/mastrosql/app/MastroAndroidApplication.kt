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
    lateinit var container: AppContainer
    //lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        //userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}

class HiltApplication: Application(), ImageLoaderFactory {
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
