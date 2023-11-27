package com.mastrosql.app.ui.navigation.main.articlesscreen.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticlesRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<ArticlesRemoteKeys>)

    @Query("SELECT * FROM articles_remote_keys WHERE article_id = :id")
    suspend fun getRemoteKeysByArticleId(id: Int): ArticlesRemoteKeys?

    @Query("DELETE FROM articles_remote_keys")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From articles_remote_keys Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?
}
