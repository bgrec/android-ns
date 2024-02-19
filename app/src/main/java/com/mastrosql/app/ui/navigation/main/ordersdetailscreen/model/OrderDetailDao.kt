package com.mastrosql.app.ui.navigation.main.ordersdetailscreen.model

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [OrderDetail] class.
 * Defines the SQL queries that Room knows how to do with our [OrderDetail] class.
 */

@Dao
interface OrderDetailDao {
    /** Specify the conflict strategy as IGNORE, when the user tries to add an
     * existing row into the database Room ignores the conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderDetail: OrderDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orderDetailList: List<OrderDetail>)

    @Update
    suspend fun update(orderDetail: OrderDetail)

    @Delete
    suspend fun delete(orderDetail: OrderDetail)

    @Query("DELETE FROM orderdetail")
    suspend fun deleteAll()

    @Query("SELECT * from orderdetail WHERE id = :id")
    fun getOrderDetailById(id: Int): Flow<OrderDetail>

    @Query("SELECT * from orderdetail ORDER BY id ASC")
    fun getAllOrderDetail(): Flow<List<OrderDetail>>

    @Query("SELECT * FROM orderdetail WHERE articleId = :articleId")
    fun getOrderDetailByArticleId(articleId: Int): Flow<OrderDetail>

    //@Query("SELECT MAX(last_updated) FROM orders AS last_updated")
    //suspend fun lastUpdated() : Long

    @Query("SELECT * FROM orderdetail")
    fun getPagedOrderDetail(): PagingSource<Int, OrderDetail>

    @Query("SELECT * FROM orderdetail")
    fun getOrdersList(): List<OrderDetail>

}