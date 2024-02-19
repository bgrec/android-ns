package com.mastrosql.app.ui.navigation.main.articlesscreen.model

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [Article] class.
 * Defines the SQL queries that Room knows how to do with our [Article] class.
 */

@Dao
interface ArticlesDao {
    /** Specify the conflict strategy as IGNORE, when the user tries to add an
    * existing row into the database Room ignores the conflict.
    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<Article>)

    @Update
    suspend fun update(article: Article)

    @Delete
    suspend fun delete(article: Article)

    @Query("DELETE FROM articles")
    suspend fun deleteAll()

    @Query("SELECT * from articles WHERE id = :id")
    fun getArticleById(id: Int): Flow<Article>

    @Query("SELECT * from articles ORDER BY id ASC")
    fun getAllArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE description LIKE :query")
    fun getArticlesByDescription(query: String): Flow<Article>

    //@Query("SELECT MAX(last_updated) FROM articles AS last_updated")
    //suspend fun lastUpdated() : Long

    @Query("SELECT * FROM articles")
    fun getPagedArticles(): PagingSource<Int, Article>

    @Query("SELECT * FROM articles")
    fun getArticlesList(): List<Article>

}