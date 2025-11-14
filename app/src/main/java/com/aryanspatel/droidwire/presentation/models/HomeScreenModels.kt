package com.aryanspatel.droidwire.presentation.models

import com.aryanspatel.droidwire.domain.model.Category

// UI-friendly model for each row
data class ArticleUi(
    val id: String,
    val title: String,
    val summary: String?,      // preview (may be null)
    val thumbUrl: String?,     // small image if available
    val source: String,
    val publishedAt: Long,     // epoch millis
    val contentUrl: String,
    val timeAgo: String,       // preformatted “5m ago”
    val saved: Boolean         // may show a small badge/star later
)

data class HomeUiState(
    val selected: Category = Category.ANDROID,
    val query: String = "",                      // optional search within category
    val isRefreshing: Boolean = false,           // for swipe-to-refresh (when you add network)
    val totalCount: Int = 0,                     // debug/empty-state helpers
    val categoryCount: Int = 0,                  // rows for current category
    val error: String? = null
)

// One-off UI events (navigation, toasts)
sealed interface HomeEvent {
    data class OpenDetail(val articleId: String) : HomeEvent
    data class ShowMessage(val text: String) : HomeEvent
    data object RequestPostNotificationsPermission : HomeEvent
}