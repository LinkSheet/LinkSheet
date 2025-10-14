package fe.linksheet

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

interface BroadcastEventBus {
    fun register(handler: IntentEventHandler)
    fun unregister(handler: IntentEventHandler)
}

interface IntentEventHandler {
    val filter: IntentFilter
    fun onReceive(context: Context, intent: Intent)
}
