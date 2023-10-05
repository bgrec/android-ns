package com.mastrosql.app.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mastrosql.app.R
import retrofit2.HttpException

private const val TAG = "SyncDataWorker"

class SyncDataWorker<T>(
    appContext: Context,
    workerParams: WorkerParameters,
    private val dataSyncOperations: DataSyncOperations<T>
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Fetch data from the server using the provided dataSyncOperations
            val dataFromServer = dataSyncOperations.fetchDataFromServer()

            // Insert or update the data using the provided dataSyncOperations
            dataSyncOperations.insertOrUpdateData(dataFromServer)

            makeStatusNotification(
                applicationContext.resources.getString(R.string.sync_data),
                applicationContext
            )

            Log.i(TAG, "Data synced successfully")
            Result.success()

        } catch (e: HttpException) {
            Result.retry() // Retry the worker on HTTP exceptions
        } catch (e: Exception) {
            Result.failure()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error syncing data")
            throwable.printStackTrace()
            Result.failure()
        }

    }


    //val customersRepository = NetworkCustomersMasterDataRepository()
}