package com.aryanspatel.droidwire.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetailDto(
    val id: String,
    val title: String,
    val source: String,
    val publishedAt: Long,
    val description: String,
    val images: List<String> = emptyList(),
    val originalUrl: String? = null
)
