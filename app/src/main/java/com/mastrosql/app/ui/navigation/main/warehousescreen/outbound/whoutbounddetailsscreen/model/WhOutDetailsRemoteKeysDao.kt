package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WhOutDetailsRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<WhOutDetailsRemoteKeys>)

    @Query("SELECT * FROM wh_outbound_details_remote_keys WHERE wh_outbound_details_id = :id")
    suspend fun getRemoteKeysByWhOutId(id: Int): WhOutDetailsRemoteKeys?

    @Query("DELETE FROM wh_outbound_details_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT created_at FROM wh_outbound_details_remote_keys ORDER BY created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}
