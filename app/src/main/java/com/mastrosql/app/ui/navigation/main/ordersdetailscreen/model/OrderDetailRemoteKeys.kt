package com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_detail_remote_keys")
data class OrderDetailRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "order_detail_id")
    val orderDetailId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
