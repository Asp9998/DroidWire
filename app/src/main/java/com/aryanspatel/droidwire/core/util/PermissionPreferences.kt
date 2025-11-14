package com.aryanspatel.droidwire.core.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object PermissionPreferences {
    private val Context.dataStore by preferencesDataStore("prefs")

    val askedNotification = booleanPreferencesKey("asked_notification")

    suspend fun setAsked(context: Context) {
        context.dataStore.edit { it[askedNotification] = true }
    }

    fun hasAsked(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[askedNotification] ?: false }
}