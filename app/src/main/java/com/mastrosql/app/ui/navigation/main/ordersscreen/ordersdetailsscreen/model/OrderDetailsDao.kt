package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.OrderDetails
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [OrderDetailsItem] class.
 * Defines the SQL queries that Room knows how to do with our [OrderDetailsItem] class.
 */

@Dao
interface OrderDetailsDao {
    /** Specify the conflict strategy as IGNORE, when the user tries to add an
     * existing row into the database Room ignores the conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insert(orderDetailsItem: OrderDetailsItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orderDetailsItemList: List<OrderDetailsItem>)

    @Update
    suspend fun update(orderDetailsItem: OrderDetailsItem)

    @Delete
    suspend fun delete(orderDetailsItem: OrderDetailsItem)

    @Query("DELETE FROM order_details")
    suspend fun deleteAll()

    @Query("SELECT * from order_details WHERE id = :id")
    fun getOrderDetailsItemById(id: Int): Flow<OrderDetails>

    @Query("SELECT * from order_details ORDER BY id ASC")
    fun getAllOrderDetailsDAO(): Flow<List<OrderDetailsItem>>

    @Query("SELECT * FROM order_details WHERE articleId = :articleId")
    fun getOrderDetailsItemByArticleId(articleId: Int): Flow<OrderDetailsItem>

    //@Query("SELECT MAX(last_updated) FROM orders AS last_updated")
    //suspend fun lastUpdated() : Long

    @Query("SELECT * FROM order_details")
    fun getPagedOrderDetails(): PagingSource<Int, OrderDetailsItem>

    @Query("SELECT * FROM order_details")
    fun getOrderDetailsItemList(): List<OrderDetailsItem>

}