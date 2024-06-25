package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.model

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [WhOutDetailsItem] class.
 * Defines the SQL queries that Room knows how to do with our [WhOutDetailsItem] class.
 */

@Dao
interface WhOutDetailsDao {
    /** Specify the conflict strategy as IGNORE, when the user tries to add an
     * existing row into the database Room ignores the conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(whOutDetailsItem: WhOutDetailsItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(whOutDetailsItemList: List<WhOutDetailsItem>)

    @Update
    suspend fun update(whOutDetailsItem: WhOutDetailsItem)

    @Delete
    suspend fun delete(whOutDetailsItem: WhOutDetailsItem)

    @Query("DELETE FROM warehouse_outbound_details")
    suspend fun deleteAll()

    @Query("SELECT * from warehouse_outbound_details WHERE id = :id")
    fun getWhOutDetailsItemById(id: Int): Flow<WhOutDetailsItem>

    @Query("SELECT * from warehouse_outbound_details ORDER BY id ASC")
    fun getAllWhOutDetailsDAO(): Flow<List<WhOutDetailsItem>>

    @Query("SELECT * FROM warehouse_outbound_details WHERE articleId = :articleId")
    fun getWhOutDetailsItemByArticleId(articleId: Int): Flow<WhOutDetailsItem>

    @Query("SELECT MAX(last_updated) FROM warehouse_outbound_details AS last_updated")
    suspend fun lastUpdated(): Long

    @Query("SELECT * FROM warehouse_outbound_details")
    fun getPagedWhOutDetails(): PagingSource<Int, WhOutDetailsItem>

    @Query("SELECT * FROM warehouse_outbound_details")
    fun getWhOutDetailsItemList(): List<WhOutDetailsItem>
}