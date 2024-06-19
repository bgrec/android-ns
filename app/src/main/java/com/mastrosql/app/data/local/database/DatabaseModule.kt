/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mastrosql.app.data.local.database

import android.content.Context
import androidx.room.Room
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)

/**
 * Dagger module that provides dependencies related to the application database.
 * This module provides methods to create and provide instances of the [AppDatabase]
 * and its associated DAOs.
 */
class DatabaseModule {

    /**
     * Provides an instance of [ItemDao] from the given [AppDatabase].
     */
    @Provides
    fun provideMastroDbDao(appDatabase: AppDatabase): ItemDao {
        return appDatabase.itemDao()
    }
    @Provides
    @Singleton

    /**
     * Provides an instance of [AppDatabase] using the Room database builder.
     */
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }
}
