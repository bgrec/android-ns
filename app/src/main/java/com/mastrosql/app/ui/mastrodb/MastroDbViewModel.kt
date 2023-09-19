

package com.mastrosql.app.ui.mastrodb
/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.local.database.MastroDbRepository
import com.mastrosql.app.ui.mastrodb.MastroDbUiState.Error
import com.mastrosql.app.ui.mastrodb.MastroDbUiState.Loading
import com.mastrosql.app.ui.mastrodb.MastroDbUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MastroDbViewModel @Inject constructor(
    private val mastroDbRepository: MastroDbRepository
) : ViewModel() {

    val uiState: StateFlow<MastroDbUiState> = mastroDbRepository
        .mastroDbs.map<List<String>, MastroDbUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addMastroDb(name: String) {
        viewModelScope.launch {
            mastroDbRepository.add(name)
        }
    }
}

sealed interface MastroDbUiState {
    object Loading : MastroDbUiState
    data class Error(val throwable: Throwable) : MastroDbUiState
    data class Success(val data: List<String>) : MastroDbUiState
}
*/