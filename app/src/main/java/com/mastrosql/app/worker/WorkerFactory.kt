package com.mastrosql.app.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.mastrosql.app.data.AppContainer

class WorkerFactory (private val appContainer: AppContainer) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            CustomersMasterDataWorker::class.java.name ->
                CustomersMasterDataWorker(appContext, workerParameters, appContainer)
            // Add other workers if needed
            else -> null
        }
    }
}