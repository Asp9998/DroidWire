package com.aryanspatel.droidwire.domain.usecase

import android.content.Context
import android.content.Intent

fun shareToken(context: Context, token: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, token)
        putExtra(Intent.EXTRA_SUBJECT, "My FCM Token")
    }

    context.startActivity(
        Intent.createChooser(intent, "Share FCM Token")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}