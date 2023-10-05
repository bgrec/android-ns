package com.mastrosql.app.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mastrosql.app.R

/**
 * Cleans up temporary files generated during blurring process
 */
private const val TAG = "CleanupWorker"

class CleanupWorker<T>(
    appContext: Context,
    workerParams: WorkerParameters,
    private val dataSyncOperations: DataSyncOperations<T>
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {
        /** Makes a notification when the work starts and slows down the work so that it's easier
         * to see each WorkRequest start, even on emulated devices
         */
        /*TODO: Change the notification message from "Cleaning up temporary files"*/
        makeStatusNotification(
            applicationContext.resources.getString(R.string.cleaning_up_files),
            applicationContext
        )

        return try {
            //delay(DELAY_TIME_MILLIS)

            dataSyncOperations.deleteData()
            Result.success()

        } catch (exception: Exception) {
            Log.e(
                TAG,
                applicationContext.resources.getString(R.string.error_cleaning_file),
                exception
            )
            Result.failure()
        }
    }
}
