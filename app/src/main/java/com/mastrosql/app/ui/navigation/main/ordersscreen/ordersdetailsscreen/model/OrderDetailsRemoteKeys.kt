package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_details_remote_keys")
data class OrderDetailsRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "order_details_id")
    val orderDetailId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
