package com.aryanspatel.droidwire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aryanspatel.droidwire.core.notif.NotificationPermissionRequester
import com.aryanspatel.droidwire.presentation.navigation.NavGraph
import com.aryanspatel.droidwire.ui.theme.DroidWireTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DroidWireTheme {
                NotificationPermissionRequester()
                NavGraph()
            }
        }
    }
}