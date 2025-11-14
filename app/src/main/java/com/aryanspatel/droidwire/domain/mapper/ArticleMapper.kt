package com.aryanspatel.droidwire.domain.mapper

import com.aryanspatel.droidwire.data.local.entity.ArticleEntity
import com.aryanspatel.droidwire.domain.model.Article
import com.aryanspatel.droidwire.presentation.models.ArticleDetailUi
import com.aryanspatel.droidwire.presentation.models.ArticleUi

fun ArticleEntity.toDomain() : Article  = Article(
    id = id,
    title = title,
    summary = summary,
    thumbUrl = thumbUrl,
    source = source,
    url = url,
    contentUrl = contentUrl,
    publishedAt = publishedAt,
    category = category,
    saved = saved
)

fun Article.toUi(now: Long = System.currentTimeMillis()): ArticleUi =
    ArticleUi(
        id = id,
        title = title,
        summary = summary,
        thumbUrl = thumbUrl ?: imageUrlFallback(),
        source = source,
        publishedAt = publishedAt,
        contentUrl = contentUrl ?: "",
        timeAgo = formatTimeAgo(publishedAt, now),
        saved = saved
    )

fun Article.toDetailUi(
    description: String? = null,
    images: List<String> = emptyList(),
    originalUrl: String? = null
): ArticleDetailUi = ArticleDetailUi(
    id = id,
    title = title,
    source = source,
    publishedAt = publishedAt,
    timeAgo = formatTimeAgo(
        publishedAt,
        now = System.currentTimeMillis()
    ),
    saved = saved,
    coverThumbUrl = images.firstOrNull() ?: thumbUrl,
    gallery = images.ifEmpty { listOfNotNull(thumbUrl) },
    description = description ?: summary,
    contentUrl = contentUrl,
    originalUrl = originalUrl
)

private fun Article.imageUrlFallback(): String? = url // or null
fun formatTimeAgo(ts: Long, now: Long): String {
    val diff = (now - ts).coerceAtLeast(0)
    val min = diff / 60_000
    val hr = diff / 3_600_000
    return when {
        min < 1 -> "just now"
        min < 60 -> "${min}m ago"
        hr < 24 -> "${hr}h ago"
        else -> "${diff / 86_400_000}d ago"
    }
}


