package com.aryanspatel.droidwire.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aryanspatel.droidwire.data.local.dao.ArticleDao
import com.aryanspatel.droidwire.data.local.dao.NotificationLogDao
import com.aryanspatel.droidwire.data.local.entity.ArticleEntity
import com.aryanspatel.droidwire.data.local.entity.NotificationLogEntity

@Database(
    entities = [ArticleEntity::class, NotificationLogEntity::class],
    version = 1,
    exportSchema = true
)
abstract class ApplicationDatabase: RoomDatabase() {
    abstract fun articleDao() : ArticleDao
    abstract fun notificationLogDao() : NotificationLogDao
}