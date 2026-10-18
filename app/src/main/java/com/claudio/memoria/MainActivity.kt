package com.claudio.memoria

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.claudio.memoria.di.AppContainer
import com.claudio.memoria.notifications.NotificationHelper
import com.claudio.memoria.ui.IntentDestination
import com.claudio.memoria.ui.MemoriaRoot
import com.claudio.memoria.ui.Routes
import com.claudio.memoria.ui.theme.MemoriaTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {

    private val _destination = MutableStateFlow<IntentDestination?>(null)
    val destination: StateFlow<IntentDestination?> = _destination.asStateFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readDestination(intent)
        val container: AppContainer = (application as MemoriaApp).container
        setContent {
            MemoriaTheme {
                MemoriaRoot(container = container, destination = destination)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readDestination(intent)
    }

    private fun readDestination(intent: Intent?) {
        val extra = intent?.getStringExtra(NotificationHelper.EXTRA_DESTINATION) ?: return
        val route = when (extra) {
            NotificationHelper.DESTINATION_UPDATE -> Routes.UPDATE
            else -> Routes.DAILY
        }
        _destination.value = IntentDestination(route, System.nanoTime())
    }
}
