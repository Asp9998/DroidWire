package com.aryanspatel.droidwire.core.notif

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.aryanspatel.droidwire.core.util.PermissionPreferences
import kotlinx.coroutines.launch

@Composable
fun NotificationPermissionRequester() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val hasAsked by PermissionPreferences.hasAsked(context)
        .collectAsState(initial = false)

    // Launcher
    val launcher = rememberLauncherForActivityResult (
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        scope.launch {
            PermissionPreferences.setAsked(context)
        }

        if (granted) {
            Log.d("Permission", "Notification permission granted")
        } else {
            Log.d("Permission", "Notification permission denied")
        }
    }

    LaunchedEffect(Unit) {
        // Only ask on Android 13+ AND only if not asked before
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasAsked) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
