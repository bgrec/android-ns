package com.mastrosql.app.ui.navigation.main.customersscreen.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomersRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<CustomersRemoteKeys>)

    @Query("SELECT * FROM customers_remote_keys WHERE customer_id = :id")
    suspend fun getRemoteKeysByCustomerId(id: Int): CustomersRemoteKeys?

    @Query("DELETE FROM customers_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From customers_remote_keys Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}
