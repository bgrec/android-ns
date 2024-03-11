package com.mastrosql.app.ui.navigation.main.customersscreen.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers_remote_keys")
data class CustomersRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "customer_id")
    val customerId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
