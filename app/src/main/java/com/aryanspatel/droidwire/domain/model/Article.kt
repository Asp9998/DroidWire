package com.aryanspatel.droidwire.domain.model

data class Article(
    val id: String,
    val title: String,
    val summary: String?,     // preview text (optional)
    val thumbUrl: String?,    // preview image
    val source: String,
    val url: String,          // original link (if any)
    val contentUrl: String?,  // backend endpoint to fetch full article
    val publishedAt: Long,
    val category: String,
    val saved: Boolean
)
