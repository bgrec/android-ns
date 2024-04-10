package com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers_destinations_remote_keys")
data class DestinationsRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "destination_id")
    val destinationId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
