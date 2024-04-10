package com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationsDataDao {
    /** Specify the conflict strategy as IGNORE, when the user tries to add an
     * existing row into the database Room ignores the conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(destinationsData: DestinationData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(destinations: List<DestinationData>)

    @Update
    suspend fun update(destinationsData: DestinationData)

    @Delete
    suspend fun delete(destinationsData: DestinationData)

    @Query("DELETE FROM customers_destinations")
    suspend fun deleteAll()

    /*@Query("SELECT * from customers WHERE id = :id")
    fun getCustomerMasterDataById(id: Int): Flow<CustomerMasterData>*/

    /*@Query("SELECT * from customers ORDER BY id ASC")
    fun getAllCustomers(): Flow<List<CustomerMasterData>>*/

    @Query("SELECT * FROM customers_destinations WHERE destinationName LIKE :query")
    fun getDestinationByName(query: String): Flow<DestinationData>

    @Query("SELECT MAX(last_updated) FROM customers_destinations AS last_updated")
    suspend fun lastUpdated(): Long

    @Query("SELECT * FROM customers_destinations")
    fun getPagedDestinations(): PagingSource<Int, DestinationData>

    @Query("SELECT * FROM customers_destinations")
    fun getDestinationsDataList(): List<DestinationData>

}