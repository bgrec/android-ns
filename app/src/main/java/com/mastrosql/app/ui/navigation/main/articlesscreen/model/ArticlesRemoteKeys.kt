package com.mastrosql.app.ui.navigation.main.articlesscreen.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles_remote_keys")
data class ArticlesRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "article_id")
    val articleId: Int,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
