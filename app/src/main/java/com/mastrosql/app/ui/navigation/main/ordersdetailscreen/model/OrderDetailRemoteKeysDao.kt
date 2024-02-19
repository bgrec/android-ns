package com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderDetailRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<OrderDetailRemoteKeys>)

    @Query("SELECT * FROM order_detail_remote_keys WHERE order_detail_id = :id")
    suspend fun getRemoteKeysByOrderId(id: Int): OrderDetailRemoteKeys?

    @Query("DELETE FROM order_detail_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From order_detail_remote_keys Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}
