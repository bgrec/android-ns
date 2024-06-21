package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WhOutboundRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<WhOutboundRemoteKeys>)

    @Query("SELECT * FROM wh_outbound_remote_keys WHERE  outbound_id = :id")
    suspend fun getRemoteKeysByOutboundId(id: Int): WhOutboundRemoteKeys?

    @Query("DELETE FROM wh_outbound_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT created_at From wh_outbound_remote_keys ORDER BY created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}
