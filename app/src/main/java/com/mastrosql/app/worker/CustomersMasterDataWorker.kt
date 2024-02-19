package com.mastrosql.app.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mastrosql.app.data.AppContainer
import retrofit2.HttpException

private const val TAG = "SyncDataWorker"

/* pass only  context and workerParams if default initializer is used */
class CustomersMasterDataWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val appContainer: AppContainer
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {

        /*makeStatusNotification(
            applicationContext.resources.getString(R.string.cleaning_up_files),
            applicationContext
        )*/

            return try {
                Log.d(TAG, "Worker started at ${System.currentTimeMillis()}")
                // Fetch data from the server using the provided dataSyncOperations
                val mastroAndroidApiService = appContainer.customerMasterDataWorkManagerRepository
                val dataFromServer = mastroAndroidApiService.getCustomersMasterData()
                //dataSyncOperations.fetchDataFromServer()

                // Insert or update the data using the provided dataSyncOperations
                //dataSyncOperations.insertOrUpdateData(dataFromServer)

                // makeStatusNotification(
                // applicationContext.resources.getString(R.string.sync_data),
                 //applicationContext
                //)

                Log.i(TAG, "Data synced successfully")
                //val outputData = workDataOf("data" to dataFromServer)

                Log.d(TAG, "Worker started at ${System.currentTimeMillis()}")
                Result.success()

            } catch (e: HttpException) {
                //Result.retry() // Retry the worker on HTTP exceptions
                Result.failure()
            } catch (e: Exception) {
                Result.failure()
            } catch (throwable: Throwable) {
                Log.e(TAG, "Error syncing data")
                throwable.printStackTrace()
                Result.failure()
            }
        }
    }

