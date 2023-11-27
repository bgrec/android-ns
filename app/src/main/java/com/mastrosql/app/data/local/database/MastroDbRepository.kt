
package com.mastrosql.app.data.local.database

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