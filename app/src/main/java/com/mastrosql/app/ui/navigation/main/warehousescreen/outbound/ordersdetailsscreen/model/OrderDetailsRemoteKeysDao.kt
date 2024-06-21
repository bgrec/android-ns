package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.ordersdetailsscreen.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderDetailsRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<OrderDetailsRemoteKeys>)

    @Query("SELECT * FROM order_details_remote_keys WHERE order_details_id = :id")
    suspend fun getRemoteKeysByOrderId(id: Int): OrderDetailsRemoteKeys?

    @Query("DELETE FROM order_details_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From order_details_remote_keys Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}
