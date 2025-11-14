package com.aryanspatel.droidwire.data.repository

import com.aryanspatel.droidwire.data.local.dao.ArticleDao
import com.aryanspatel.droidwire.data.remote.api.ArticleContentApi
import com.aryanspatel.droidwire.data.remote.dto.ArticleDetailDto
import com.aryanspatel.droidwire.domain.mapper.toDomain
import com.aryanspatel.droidwire.domain.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArticleDetailFetchRepository @Inject constructor(
    private val api: ArticleContentApi,
    private val dao: ArticleDao
) {
    suspend fun fetchDetail(contentUrl: String): ArticleDetailDto =
        api.getDetail(contentUrl)

    suspend fun getArticleById(id: String): Article? =
        dao.getArticleById(id)?.toDomain()

    fun observeArticleById(id: String): Flow<Article?> =
        dao.observeById(id).map { it?.toDomain() }
}