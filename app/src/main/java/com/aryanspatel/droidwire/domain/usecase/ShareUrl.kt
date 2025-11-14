package com.aryanspatel.droidwire.domain.usecase

import android.content.Context
import android.content.Intent

fun shareURL(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
    }

    val chooser = Intent.createChooser(intent, "Share article link")
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    context.startActivity(chooser)
}