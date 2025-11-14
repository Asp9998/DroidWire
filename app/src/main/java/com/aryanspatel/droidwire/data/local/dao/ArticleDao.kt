package com.aryanspatel.droidwire.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aryanspatel.droidwire.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles WHERE category=:category ORDER BY published_at DESC LIMIT :limit OFFSET :offset")
    suspend fun page(category: String, limit: Int, offset: Int): List<ArticleEntity>

    @Query("SELECT * FROM articles ORDER BY published_at DESC")
    fun observeAll(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<ArticleEntity?>

    @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
    suspend fun getArticleById(id: String): ArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ArticleEntity>)

    @Query("UPDATE articles SET saved = :saved WHERE id = :id")
    suspend fun setSaved(id: String, saved: Boolean)

    // paging
    @Query("SELECT * FROM articles WHERE category = :category ORDER BY published_at DESC")
    fun pagingSource(category: String): PagingSource<Int, ArticleEntity>
}