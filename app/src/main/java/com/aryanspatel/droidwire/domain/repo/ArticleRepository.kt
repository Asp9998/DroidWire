package com.aryanspatel.droidwire.domain.repo

import androidx.paging.PagingData
import com.aryanspatel.droidwire.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun observeAll(): Flow<List<Article>>
    fun observeById(id: String): Flow<Article?>
    suspend fun upsert(article: List<Article>)
    suspend fun setSaved(id: String, saved: Boolean)
    suspend fun refresh(contentUrl: String) : Boolean
    fun paged(category: String): Flow<PagingData<Article>>
}