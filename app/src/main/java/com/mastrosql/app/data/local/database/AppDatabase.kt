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
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationsDataDao
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationsLinksTypeConverter
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationsMetadataTypeConverter
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationsRemoteKeys
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
    //Change version number if you want to update the schema
    version = 4,
    exportSchema = true, // false
    entities = [
        Item::class,
        Article::class,
        ArticlesRemoteKeys::class,
        CustomersRemoteKeys::class,
        CustomerMasterData::class,
        Order::class,
        OrdersRemoteKeys::class,
        OrderDetailsItem::class,
        OrderDetailsRemoteKeys::class,
        DestinationData::class,
        DestinationsRemoteKeys::class,
    ],
    /*autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]*/
)

/**
 * Annotation to specify the type converters used by Room for custom data types when storing and retrieving from the database.
 * Each converter class listed should implement the TypeConverter interface to facilitate conversion to and from database-compatible types.
 */
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
    DestinationsMetadataTypeConverter::class,
    DestinationsLinksTypeConverter::class
)

/**
 * Abstract database class that extends RoomDatabase and provides access to various DAOs for interacting with
 * different entities in the database.
 */
abstract class AppDatabase : RoomDatabase() {

    //abstract fun mastroDbDao(): MastroDbDao

    /**
     * Provides access to the DAO (Data Access Object) for interacting with the Item entity in the database.
     */
    abstract fun itemDao(): ItemDao

    /**
     * Provides access to the DAO (Data Access Object) for interacting with the CustomersMasterData entity in the database.
     */
    abstract fun customersMasterDataDao(): CustomersMasterDataDao

    /**
     * Provides access to the DAO (Data Access Object) for interacting with the CustomersRemoteKeys entity in the database.
     */
    abstract fun customersRemoteKeysDao(): CustomersRemoteKeysDao

    /**
     * Provides access to the DAO (Data Access Object) for interacting with the Articles entity in the database.
     */
    abstract fun articlesDao(): ArticlesDao

    /**
     * Provides access to the DAO (Data Access Object) for interacting with the remote keys related to the Articles entity in the database.
     */
    abstract fun articlesRemoteKeysDao(): ArticlesRemoteKeysDao

    /**
     * Provides access to the DAO (Data Access Object) for interacting with the Orders entity in the database.
     */
    abstract fun ordersDao(): OrdersDao

    /**
     * Provides access to the DAO (Data Access Object) for interacting with the OrdersRemoteKeys entity in the database.
     */
    abstract fun ordersRemoteKeysDao(): OrdersRemoteKeysDao

    /**
     * Provides access to the DAO (Data Access Object) for interacting with the OrderDetails entity in the database.
     */
    abstract fun orderDetailsDao(): OrderDetailsDao

    /**
     * Provides access to the DAO (Data Access Object) for interacting with the OrderDetailsRemoteKeys entity in the database.
     */
    abstract fun orderDetailRemoteKeysDao(): OrderDetailsRemoteKeysDao

    /**
     * Provides access to the DAO (Data Access Object) for interacting with the DestinationsData entity in the database.
     */
    abstract fun destinationsDataDao(): DestinationsDataDao


    /**
     * The singleton instance of the AppDatabase class, which provides access to the application's Room database.
     * It initializes and manages the database creation process using Room's databaseBuilder.
     */
    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        /**
         * Retrieves the singleton instance of the AppDatabase. If an instance already exists, it returns it;
         * otherwise, it creates a new database instance using Room's databaseBuilder.
         */
        fun getInstance(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.

            /**
             * Retrieves the singleton instance of the AppDatabase. If an instance already exists, it returns it;
             * otherwise, it creates a new database instance using Room's databaseBuilder.
             */
            return Instance ?: synchronized(this) {

                /**
                 * Creates a new Room database builder instance for AppDatabase.
                 */
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

/*
val MIGRATION_1_2 = object : Migration(1, 2) {
  override fun migrate(database: SupportSQLiteDatabase) {
    database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " +
      "PRIMARY KEY(`id`))")
  }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
  override fun migrate(database: SupportSQLiteDatabase) {
    database.execSQL("ALTER TABLE Book ADD COLUMN pub_year INTEGER")
  }
}

Room.databaseBuilder(applicationContext, MyDb::class.java, "database-name")
  .addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
 */