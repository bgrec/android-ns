package com.mastrosql.app.ui.navigation.main.ordersscreen.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders_remote_keys")
data class OrdersRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "order_id")
    val orderId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
