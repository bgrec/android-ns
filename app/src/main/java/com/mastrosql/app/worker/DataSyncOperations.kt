package com.mastrosql.app.worker

interface DataSyncOperations<T> {
    suspend fun fetchDataFromServer(): List<T>
    suspend fun insertOrUpdateData(data: List<T>)

    suspend fun deleteData()
}