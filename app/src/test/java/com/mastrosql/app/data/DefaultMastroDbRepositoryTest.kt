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

package com.mastrosql.app.data

import com.mastrosql.app.data.datasource.database.DefaultMastroDbRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.mastrosql.app.data.local.database.MastroDb
import com.mastrosql.app.data.local.database.MastroDbDao

/**
 * Unit tests for [DefaultMastroDbRepository].
 */
// TODO: Remove when stable
class DefaultMastroDbRepositoryTest {

    @Test
    fun mastroDbs_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultMastroDbRepository(FakeMastroDbDao())

        repository.add("Repository")

        assertEquals(repository.mastroDbs.first().size, 1)
    }

}

private class FakeMastroDbDao : MastroDbDao {

    private val data = mutableListOf<MastroDb>()

    override fun getMastroDbs(): Flow<List<MastroDb>> = flow {
        emit(data)
    }

    override suspend fun insertMastroDb(item: MastroDb) {
        data.add(0, item)
    }
}
