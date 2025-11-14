package com.aryanspatel.droidwire.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aryanspatel.droidwire.data.local.dao.ArticleDao
import com.aryanspatel.droidwire.data.local.entity.ArticleEntity
import com.aryanspatel.droidwire.domain.mapper.toDomain
import com.aryanspatel.droidwire.domain.model.Article
import com.aryanspatel.droidwire.domain.repo.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ArticleRepositoryImp @Inject constructor(
    private val dao: ArticleDao
): ArticleRepository {

    override fun observeAll(): Flow<List<Article>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Article?> =
        dao.observeById(id).map { it?.toDomain() }


    override suspend fun upsert(article: List<Article>) {
        val entities = article.map {
            ArticleEntity(
                id = it.id,
                title = it.title,
                summary = it.summary,
                thumbUrl = it.thumbUrl,
                source = it.source,
                url = it.url,
                contentUrl = it.contentUrl,
                publishedAt = it.publishedAt,
                category = it.category,
                saved = it.saved
            )
        }
        dao.upsertAll(entities)
    }

    override suspend fun setSaved(id: String, saved: Boolean) =
        dao.setSaved(id, saved)

    override suspend fun refresh(contentUrl: String): Boolean {
       //  Retrofit call → map → persist heavy content table
        return true
    }

    override fun paged(category: String): Flow<PagingData<Article>> {
        Log.d("ArticleAndroid", "paged: Article fetched from repo")
        return Pager(PagingConfig(pageSize = 20, prefetchDistance = 5)){
            dao.pagingSource(category)
        }.flow.map { it.map(ArticleEntity::toDomain)}
    }
}