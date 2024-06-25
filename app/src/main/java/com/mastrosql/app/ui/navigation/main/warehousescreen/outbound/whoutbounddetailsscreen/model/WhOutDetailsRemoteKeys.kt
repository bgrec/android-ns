package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wh_outbound_details_remote_keys")
data class WhOutDetailsRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "wh_outbound_details_id")
    val whOutDetailId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
