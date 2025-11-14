package com.aryanspatel.droidwire.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aryanspatel.droidwire.core.util.PermissionPreferences
import com.aryanspatel.droidwire.core.util.TopicPreferences
import com.aryanspatel.droidwire.presentation.models.SettingsEvent
import com.aryanspatel.droidwire.presentation.models.SettingsUiState
import com.aryanspatel.droidwire.presentation.models.TopicToggle
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    private val context = application.applicationContext

    private val defaultTopics = listOf(
        TopicToggle("android", "Android", true),
        TopicToggle("jetpack", "Jetpack", true),
        TopicToggle("kotlin", "Kotlin", true),
        TopicToggle("official", "Official", true)
    )

    val hasAskedNotification: StateFlow<Boolean> =
        PermissionPreferences
            .hasAsked(context)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                false
            )
    private val _uiState = MutableStateFlow(
        SettingsUiState(
            notificationsEnabled = hasAskedNotification.value,
            fcmToken = "",
            isFetchingToken = false
        )
    )

    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<SettingsEvent?>(null)
    val events: StateFlow<SettingsEvent?> = _events.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy( fcmToken = FirebaseMessaging.getInstance().token.await()) }

            val updated = defaultTopics.map { topic ->
                val subscribed = TopicPreferences
                    .isSubscribed(context, topic.key)
                    .first()

                topic.copy(subscribed = subscribed)
            }

            _uiState.update { it.copy(topics = updated) }
        }
    }
    fun onTopicToggle(topicKey: String) {
        viewModelScope.launch {
            // Set busy state
            _uiState.value = _uiState.value.copy(
                topics = _uiState.value.topics.map {
                    if (it.key == topicKey) it.copy(busy = true) else it
                }
            )

            // Simulate API call
            delay(600)

            // Update subscription state
            _uiState.value = _uiState.value.copy(
                topics = _uiState.value.topics.map {
                    if (it.key == topicKey) {
                        if(it.subscribed){
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(topicKey).await()
                            TopicPreferences.setSubscribed(context, topicKey, false)
                            Log.d("FCM_TOKEN", "onTopicToggle: unsubscribed to $topicKey")
                        }else{
                            FirebaseMessaging.getInstance().subscribeToTopic(topicKey).await()
                            TopicPreferences.setSubscribed(context, topicKey, true)

                            Log.d("FCM_TOKEN", "onTopicToggle: subscribed successfully to $topicKey")
                        }
                        it.copy(subscribed = !it.subscribed, busy = false)
                    } else it
                }
            )

            val topic = _uiState.value.topics.find { it.key == topicKey }
            topic?.let {
                _events.value = SettingsEvent.ShowMessage(
                    if (it.subscribed) "Subscribed to ${it.label}" else "Unsubscribed from ${it.label}"
                )
            }
        }
    }

    fun onEventConsumed() {
        _events.value = null
    }

    fun onCopyToken(token: String) {
        _events.value = SettingsEvent.CopyToken(token)
    }
    fun onShareToken(token: String) {
        _events.value = SettingsEvent.ShareToken(token)
    }
}