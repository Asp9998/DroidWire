package com.aryanspatel.droidwire.core.notif

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.aryanspatel.droidwire.MainActivity
import com.aryanspatel.droidwire.R
import okhttp3.internal.notify

object NotificationBuilder{
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun postBreaking(
        context: Context,
        articleId: String,
        title: String,
        summary: String,
        category: String,  // android | jetpack | kotlin | official
        url: String?,
        imageUrl: String? = null
    ) {

        val channelId = when(category.lowercase()){
            "jetpack" -> NewsChannel.JETPACK
            "kotlin" -> NewsChannel.KOTLIN
            "official" -> NewsChannel.OFFICIAL
            else -> NewsChannel.ANDROID
        }

        // deeplink to detail/{id}
        val uri = "app://droidwire/detail/$articleId".toUri()
        val contentIntent = Intent(Intent.ACTION_VIEW, uri, context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.android)
            .setContentTitle(title.ifBlank { "Android News" })
            .setContentText(summary)
            .setStyle(NotificationCompat.BigTextStyle().bigText(summary))
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(articleId.hashCode(), builder.build())
    }


}