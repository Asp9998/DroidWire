package com.aryanspatel.droidwire.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aryanspatel.droidwire.data.local.entity.NotificationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: NotificationLogEntity)

    @Query("SELECT * FROM notification_log ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<NotificationLogEntity>>
}