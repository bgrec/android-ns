package com.mastrosql.app.ui.navigation.main.customersScreen.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomersRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<CustomersRemoteKeys>)

    @Query("SELECT * FROM customers_remote_keys WHERE pageId = :pageId")
    suspend fun remoteKeysPageId(pageId: Long): CustomersRemoteKeys?

    @Query("DELETE FROM customers_remote_keys")
    suspend fun clearRemoteKeys()
}
