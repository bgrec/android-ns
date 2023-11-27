package com.mastrosql.app.ui.navigation.main.ordersscreen.model

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [Order] class.
 * Defines the SQL queries that Room knows how to do with our [Order] class.
 */

@Dao
interface OrdersDao {
    /** Specify the conflict strategy as IGNORE, when the user tries to add an
    * existing row into the database Room ignores the conflict.
    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<Order>)

    @Update
    suspend fun update(order: Order)

    @Delete
    suspend fun delete(order: Order)

    @Query("DELETE FROM orders")
    suspend fun deleteAll()

    @Query("SELECT * from orders WHERE id = :id")
    fun getOrderById(id: Int): Flow<Order>

    @Query("SELECT * from orders ORDER BY id ASC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE description LIKE :query")
    fun getOrdersByDescription(query: String): Flow<Order>

    //@Query("SELECT MAX(last_updated) FROM orders AS last_updated")
    //suspend fun lastUpdated() : Long

    @Query("SELECT * FROM orders")
    fun getPagedOrders(): PagingSource<Int, Order>

    @Query("SELECT * FROM orders")
    fun getOrdersList(): List<Order>

}