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
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticleLinksTypeConverter
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticleMetadataTypeConverter
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesDao
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesRemoteKeys
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.ArticlesRemoteKeysDao
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerLinksTypeConverter
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMetadataTypeConverter
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersMasterDataDao
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersRemoteKeys
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomersRemoteKeysDao
import com.mastrosql.app.ui.navigation.main.itemsScreen.ItemDao
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Item
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.ItemMetadataTypeConverter
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrderLinksTypeConverter
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrderMetadataTypeConverter
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersRemoteKeys
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.OrdersRemoteKeysDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailLinksTypeConverter
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsDao
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsMetadataTypeConverter
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsRemoteKeys
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsRemoteKeysDao


/**
 * Database class with a singleton Instance object.
 */

/*if */
@Database(
    entities = [
        Item::class,
        Article::class,
        ArticlesRemoteKeys::class,
        CustomersRemoteKeys::class,
        CustomerMasterData::class,
        Order::class,
        OrdersRemoteKeys::class,
        OrderDetailsItem::class,
        OrderDetailsRemoteKeys::class
    ],
    version = 1, exportSchema = false
)
@TypeConverters(
    ItemMetadataTypeConverter::class,
    CustomerLinksTypeConverter::class,
    CustomerMetadataTypeConverter::class,
    ArticleMetadataTypeConverter::class,
    ArticleLinksTypeConverter::class,
    OrderMetadataTypeConverter::class,
    OrderLinksTypeConverter::class,
    OrderDetailsMetadataTypeConverter::class,
    OrderDetailLinksTypeConverter::class,
)

abstract class AppDatabase : RoomDatabase() {

    //abstract fun mastroDbDao(): MastroDbDao

    abstract fun itemDao(): ItemDao
    abstract fun customersMasterDataDao(): CustomersMasterDataDao
    abstract fun customersRemoteKeysDao(): CustomersRemoteKeysDao
    abstract fun articlesDao(): ArticlesDao
    abstract fun articlesRemoteKeysDao(): ArticlesRemoteKeysDao
    abstract fun ordersDao(): OrdersDao
    abstract fun ordersRemoteKeysDao(): OrdersRemoteKeysDao
    abstract fun orderDetailsDao(): OrderDetailsDao
    abstract fun orderDetailRemoteKeysDao(): OrderDetailsRemoteKeysDao


    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration() //wipe and rebuild instead of migrating
                    .build()
                    .also {
                        Instance = it
                    }

            }
        }
    }
}