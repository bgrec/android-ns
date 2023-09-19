
package com.mastrosql.app.data.local.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
/*
interface MastroDbRepository {
    val mastroDbs: Flow<List<String>>

    suspend fun add(name: String)
}

class DefaultMastroDbRepository @Inject constructor(
    private val mastroDbDao: MastroDbDao
) : MastroDbRepository {

    override val mastroDbs: Flow<List<String>> =
        mastroDbDao.getMastroDbs().map { items -> items.map { it.name } }

    override suspend fun add(name: String) {
        mastroDbDao.insertMastroDb(MastroDb(name = name))
    }


}
*/