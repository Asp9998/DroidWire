package com.aryanspatel.droidwire.presentation.models

data class TopicToggle(
    val key: String,
    val label: String,
    val subscribed: Boolean,
    val busy: Boolean = false,
    val error: String? = null
)

data class SettingsUiState(
    val notificationsEnabled: Boolean = false,
    val backgroundRefreshEnabled: Boolean = false,
    val topics: List<TopicToggle> = listOf(
        TopicToggle("android", "Android", true),
        TopicToggle("jetpack", "Jetpack", true),
        TopicToggle("kotlin", "Kotlin", true),
        TopicToggle("official", "Official", true)
    ),
    val fcmToken: String = "",
    val isFetchingToken: Boolean = true,
    val error: String? = null
)

sealed interface SettingsEvent {
    data class ShowMessage(val text: String) : SettingsEvent
    data class CopyToken(val token: String) : SettingsEvent
    data class ShareToken(val token: String) : SettingsEvent
}