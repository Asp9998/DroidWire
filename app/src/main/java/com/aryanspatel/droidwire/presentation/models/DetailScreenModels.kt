package com.aryanspatel.droidwire.presentation.models

data class ArticleDetailUi(
    val id: String,
    val title: String,
    val source: String,
    val publishedAt: Long,
    val timeAgo: String,
    val saved: Boolean,
    val coverThumbUrl: String?,
    val gallery: List<String>,
    val description: String?,
    val contentUrl: String?,
    val originalUrl: String?
)

data class DetailUiState(
    val loading: Boolean = true,
    val article: ArticleDetailUi? = null,
    val error: String? = null
)

sealed interface DetailEvent {
    data object ShowSavedToast : DetailEvent
    data class OpenShareSheet(val url: String?) : DetailEvent
    data class OpenExternal(val url: String?) : DetailEvent
}