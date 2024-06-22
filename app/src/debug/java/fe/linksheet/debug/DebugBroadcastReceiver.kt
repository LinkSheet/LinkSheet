package fe.linksheet.debug

import android.content.*
import androidx.core.content.getSystemService
import fe.linksheet.debug.command.DebugCommand
import fe.linksheet.util.BuildType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent


class DebugBroadcastReceiver : BroadcastReceiver(), KoinComponent {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val COPY_URL_BROADCAST = "fe.linksheet.debug.COPY_URL"
        private const val RESOLVE_URL_BROADCAST = "fe.linksheet.debug.RESOLVE_URL"
        const val UPDATE_PREF_BROADCAST = "fe.linksheet.debug.UPDATE_PREF"
        const val NAVIGATE_BROADCAST = "fe.linksheet.debug.NAVIGATE"
        const val RESET_HISTORY_PREFERRED_APP_BROADCAST = "fe.linksheet.debug.RESET_HISTORY_PREFERRED_APP"
        const val DUMP_PREFERENCES_BROADCAST = "fe.linksheet.debug.DUMP_PREFERENCES"
        const val VIEW_URL_BROADCAST = "fe.linksheet.debug.VIEW_URL"
        const val DUMP_NAV_GRAPH_BROADCAST = "fe.linksheet.debug.DUMP_NAV_GRAPH"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (!BuildType.current.allowDebug) return

        val handled = DebugCommand.tryHandle(context, intent)
        if (handled) return

        if (intent.action == COPY_URL_BROADCAST) {
            val clipboardManager = context.getSystemService<ClipboardManager>()!!
            val url = intent.extras?.getString("url")
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Debug intent", url))

            return
        }
    }
}
