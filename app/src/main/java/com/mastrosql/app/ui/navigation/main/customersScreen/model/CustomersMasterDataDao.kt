package com.mastrosql.app.ui.navigation.main.customersScreen.model

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mastrosql.app.ui.navigation.main.customersScreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.itemsScreen.model.Item
import kotlinx.coroutines.flow.Flow


@Dao
interface CustomersMasterDataDao {
    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customerMasterData: CustomerMasterData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerMasterData>)

    @Update
    suspend fun update(customerMasterData: CustomerMasterData)

    @Delete
    suspend fun delete(customerMasterData: CustomerMasterData)

    @Query("SELECT * from customers WHERE id = :id")
    fun getCustomerMasterData(id: Int): Flow<CustomerMasterData>

    @Query("SELECT * from customers ORDER BY businessName ASC")
    fun getAllCustomers(): Flow<List<CustomerMasterData>>

    @Query("SELECT * FROM customers WHERE businessName LIKE :query")
    fun pagingSource(query: String): PagingSource<Int, CustomerMasterData>

    @Query("DELETE FROM customers")
    suspend fun clearAll()

}