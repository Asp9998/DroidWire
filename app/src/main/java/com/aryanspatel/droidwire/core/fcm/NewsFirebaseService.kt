package com.aryanspatel.droidwire.core.fcm

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.aryanspatel.droidwire.core.notif.NotificationBuilder
import com.aryanspatel.droidwire.data.repository.ArticleRepositoryImp
import com.aryanspatel.droidwire.domain.model.Article
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewsFirebaseService: FirebaseMessagingService() {

    @Inject
    lateinit var articleRepository: ArticleRepositoryImp

    override fun onNewToken(token: String) {
        val defaultTopics = listOf("official", "android", "kotlin", "jetpack")
        for (topic in defaultTopics) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
            Log.d("FCM", "Auto subscribed to $topic")
        }
        TokenHolder.save(token)

    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data

        val id = data["id"] ?: return
        val title = data["title"] ?: "Android News"
        val summary = data["summary"] ?: ""
        val category = data["category"] ?: "android"
//        val url = data["url"] ?: "https://android-developers.googleblog.com/"
        val url = data["url"] ?: ""
        val thumbUrl = data["thumb"] ?: ""
//        val thumbUrl = data["thumb"] ?: "https://developer.android.com/static/images/logos/android.svg"
        val contentUrl = data["contentUrl"]
        val source = data["source"] ?: "Unknown"
        val publishedAt = data["publishedAt"]?.toLongOrNull() ?: System.currentTimeMillis()

        // Optional: upsert a minimal article into Room here (later)
        // For now, just post the notification with deep link to detail/{id}
        CoroutineScope(SupervisorJob()+ Dispatchers.IO).launch {

            try {
                val entity = Article(
                    id = id,
                    title = title,
                    summary = summary,
                    source = source,        // ← fix: use source, not summary
                    url = url,
                    thumbUrl = thumbUrl,
                    contentUrl = contentUrl,
                    publishedAt = publishedAt,
                    category = category,
                    saved = false,
                )
                articleRepository.upsert(listOf(entity))
            } catch (e: Exception) {
                Log.e("FCM", "DB upsert failed", e)
            }

            NotificationBuilder.postBreaking(
                context = applicationContext,
                articleId = id,
                title = title,
                summary = summary,
                category = category,
                url = url,
                imageUrl = thumbUrl
            )
        }

    }
}

// Super-light token holder for now (replace with DataStore later)
object TokenHolder {
    @Volatile private var token: String? = null
    fun save(t: String) { token = t }
    fun get(): String? = token
}