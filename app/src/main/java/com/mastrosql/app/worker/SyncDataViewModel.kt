package com.mastrosql.app.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.mastrosql.app.data.customers.CustomersMasterDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SyncDataViewModel(private val customersRepository: CustomersMasterDataRepository) :
    ViewModel() {

    //Whether the work is BLOCKED, CANCELLED, ENQUEUED, FAILED, RUNNING or SUCCEEDED.

    val syncDataUiState: StateFlow<SyncDataUiState> = customersRepository.outputWorkInfo
        .map { info ->
            //val outputImageUri = info.outputData.getString(KEY_IMAGE_URI)
            when {
                info.state.isFinished /*&& !outputImageUri.isNullOrEmpty()*/ -> {
                    SyncDataUiState.Complete(outputMessage = "done")
                }

                info.state == WorkInfo.State.CANCELLED -> {
                    SyncDataUiState.Default
                }

                else -> SyncDataUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SyncDataUiState.Default
        )

    /*val syncDataWorker = SyncDataWorker(
        appContext = applicationContext,
        workerParams = workerParams,
        dataSyncOperations = customersRepository
    )*/

    fun syncData() {
        // customersRepository.applyBlur(blurLevel)
    }

    /**
     * Call method from repository to cancel any ongoing WorkRequest
     * */
    fun cancelWork() {
        // customersRepository.cancelWork()
    }

}

sealed interface SyncDataUiState {
    object Default : SyncDataUiState
    object Loading : SyncDataUiState
    data class Complete(val outputMessage: String) : SyncDataUiState
}