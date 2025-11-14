package com.aryanspatel.droidwire.domain.mapper

import com.aryanspatel.droidwire.data.remote.dto.ArticleDetailDto
import com.aryanspatel.droidwire.domain.model.Article
import com.aryanspatel.droidwire.presentation.models.ArticleDetailUi

fun ArticleDetailDto.toUi(existing: Article) = ArticleDetailUi(
    id = id,
    title = title,
    source = source,
    publishedAt = publishedAt,
    timeAgo = formatTimeAgo(ts = publishedAt, now = System.currentTimeMillis()),
    saved = false,
    coverThumbUrl = images.firstOrNull(),
    gallery = images,
    description = description,
    contentUrl = existing.contentUrl,
    originalUrl = originalUrl
)
