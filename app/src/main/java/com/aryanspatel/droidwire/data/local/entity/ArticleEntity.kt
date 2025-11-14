package com.aryanspatel.droidwire.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "articles",
    indices = [Index(value = ["url"], unique = true), Index(value = ["category"])])
data class ArticleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // stable id (e.g., hash(url)+publishedAt)

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "summary")
    val summary: String?,

    @ColumnInfo(name = "thumb_url")
    val thumbUrl: String?,

    @ColumnInfo(name = "source")
    val source: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "content_url")
    val contentUrl: String?,   // backend endpoint to fetch full article

    @ColumnInfo(name = "published_at")
    val publishedAt: Long,   // epoch millis

    @ColumnInfo(name = "category")
    val category: String,  // android|jetpack|kotlin|official|hn

    @ColumnInfo(name = "saved")
    val saved: Boolean = false
)
