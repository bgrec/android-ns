package com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DestinationsRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<DestinationsRemoteKeys>)

    @Query("SELECT * FROM customers_destinations_remote_keys WHERE destination_id = :id")
    suspend fun getRemoteKeysByDestinationId(id: Int): DestinationsRemoteKeys?

    @Query("DELETE FROM customers_destinations_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From customers_destinations_remote_keys Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}
