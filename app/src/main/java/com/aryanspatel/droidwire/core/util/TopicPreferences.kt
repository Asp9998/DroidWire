package com.aryanspatel.droidwire.core.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


object TopicPreferences {
    private val Context.dataStore by preferencesDataStore("topic_prefs")

    private fun keyFor(topic: String) =
        booleanPreferencesKey("topic_$topic")

    fun isSubscribed(context: Context, topic: String): Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[keyFor(topic)] ?: true        // default = subscribed
        }

    suspend fun setSubscribed(context: Context, topic: String, value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[keyFor(topic)] = value
        }
    }
}