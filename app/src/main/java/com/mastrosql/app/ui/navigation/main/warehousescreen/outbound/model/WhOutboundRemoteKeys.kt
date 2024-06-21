package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wh_outbound_remote_keys")
data class WhOutboundRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "outbound_id")
    val outboundId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
