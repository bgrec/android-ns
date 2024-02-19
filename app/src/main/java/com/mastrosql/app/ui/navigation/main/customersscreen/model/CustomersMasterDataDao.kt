package com.mastrosql.app.ui.navigation.main.customersscreen.model

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [CustomerMasterData] class.
 * Defines the SQL queries that Room knows how to do with our [CustomerMasterData] class.
 */

@Dao
interface CustomersMasterDataDao {
    /** Specify the conflict strategy as IGNORE, when the user tries to add an
    * existing row into the database Room ignores the conflict.
    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customerMasterData: CustomerMasterData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerMasterData>)

    @Update
    suspend fun update(customerMasterData: CustomerMasterData)

    @Delete
    suspend fun delete(customerMasterData: CustomerMasterData)

    @Query("DELETE FROM customers")
    suspend fun deleteAll()

    /*@Query("SELECT * from customers WHERE id = :id")
    fun getCustomerMasterDataById(id: Int): Flow<CustomerMasterData>*/

    /*@Query("SELECT * from customers ORDER BY id ASC")
    fun getAllCustomers(): Flow<List<CustomerMasterData>>*/

    @Query("SELECT * FROM customers WHERE businessName LIKE :query")
    fun getCustomersByBusinessName(query: String): Flow<CustomerMasterData>

    @Query("SELECT MAX(last_updated) FROM customers AS last_updated")
    suspend fun lastUpdated() : Long

    @Query("SELECT * FROM customers")
    fun getPagedCustomers(): PagingSource<Int, CustomerMasterData>

    @Query("SELECT * FROM customers")
    fun getCustomersMasterDataList(): List<CustomerMasterData>

}