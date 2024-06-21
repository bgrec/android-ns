package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [WarehouseOutbound] class.
 * Defines the SQL queries that Room knows how to do with our [WarehouseOutbound] class.
 */

@Dao
interface WarehouseOutboundDao {
    /** Specify the conflict strategy as IGNORE, when the user tries to add an
     * existing row into the database Room ignores the conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(whOutbound: WarehouseOutbound)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(whOutbounds: List<WarehouseOutbound>)

    @Update
    suspend fun update(whOutbound: WarehouseOutbound)

    @Delete
    suspend fun delete(whOutbound: WarehouseOutbound)

    @Query("DELETE FROM warehouse_outbound")
    suspend fun deleteAll()

    @Query("SELECT * from warehouse_outbound WHERE id = :id")
    fun getWhOutboundById(id: Int): Flow<WarehouseOutbound>

    @Query("SELECT * from warehouse_outbound ORDER BY id ASC")
    fun getAllWhOutbounds(): Flow<List<WarehouseOutbound>>

    @Query("SELECT * FROM warehouse_outbound WHERE businessName LIKE :query")
    fun getWhOutboundsByDescription(query: String): Flow<WarehouseOutbound>

    //@Query("SELECT MAX(last_updated) FROM warehouse_outbound AS last_updated")
    //suspend fun lastUpdated() : Long

    @Query("SELECT * FROM warehouse_outbound")
    fun getPagedWhOutbounds(): PagingSource<Int, WarehouseOutbound>

    @Query("SELECT * FROM warehouse_outbound")
    fun getWhOutboundsList(): List<WarehouseOutbound>

}