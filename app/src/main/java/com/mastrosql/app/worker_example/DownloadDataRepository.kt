package com.mastrosql.app.worker_example

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface DownloadDataRepository {
    val outputWorkInfo: Flow<WorkInfo?>
    fun applyBlur(blurLevel: Int)
    fun cancelWork()
}