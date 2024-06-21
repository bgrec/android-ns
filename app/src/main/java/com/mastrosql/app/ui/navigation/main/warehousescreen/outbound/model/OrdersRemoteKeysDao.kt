package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrdersRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<OrdersRemoteKeys>)

    @Query("SELECT * FROM orders_remote_keys WHERE order_id = :id")
    suspend fun getRemoteKeysByOrderId(id: Int): OrdersRemoteKeys?

    @Query("DELETE FROM orders_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From orders_remote_keys Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}
