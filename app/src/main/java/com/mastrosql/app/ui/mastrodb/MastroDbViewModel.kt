/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mastrosql.app.ui.mastrodb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastrosql.app.data.datasource.database.MastroDbRepository
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
