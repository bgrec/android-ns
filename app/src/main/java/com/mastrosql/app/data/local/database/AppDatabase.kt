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
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersRemoteKeysDao
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersMasterDataDao
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerLinksTypeConverter
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerMetadataTypeConverter
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomersRemoteKeys
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemDao
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Item
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.ItemMetadataTypeConverter
import java.util.concurrent.TimeUnit


/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Item::class, CustomerMasterData::class, CustomersRemoteKeys::class], version = 1, exportSchema = false)
@TypeConverters(
    ItemMetadataTypeConverter::class,
    CustomerMetadataTypeConverter::class,
    CustomerLinksTypeConverter::class
)

abstract class AppDatabase : RoomDatabase() {

    //abstract fun mastroDbDao(): MastroDbDao

    abstract fun itemDao(): ItemDao
    abstract fun customersMasterDataDao(): CustomersMasterDataDao
    abstract fun customersRemoteKeysDao(): CustomersRemoteKeysDao
    //abstract fun lastUpdated(): TimeUnit

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }

            }
        }
    }
}

/*
* fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .createFromAsset("database/bus_schedule.db")
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
*
* */