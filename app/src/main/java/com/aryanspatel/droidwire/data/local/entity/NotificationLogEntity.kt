package com.aryanspatel.droidwire.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "notification_log",
    indices = [Index(value = ["article_id"])])
data class NotificationLogEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,             // e.g., articleId + timestamp

    @ColumnInfo(name = "article_id")
    val articleId: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "action") // delivered|opened|saved|shared
    val action: String
)