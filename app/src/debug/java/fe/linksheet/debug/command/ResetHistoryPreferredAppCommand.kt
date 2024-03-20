package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import android.widget.Toast
import fe.linksheet.debug.DebugBroadcastReceiver
import fe.linksheet.module.repository.PreferredAppRepository
import kotlinx.coroutines.runBlocking
import org.koin.core.component.get


object ResetHistoryPreferredAppCommand : DebugCommand<ResetHistoryPreferredAppCommand>(
    DebugBroadcastReceiver.RESET_HISTORY_PREFERRED_APP_BROADCAST, ResetHistoryPreferredAppCommand::class
) {
    override fun handle(context: Context, intent: Intent) {
        val extras = requireNotNull(intent.extras) { "Extras must not be null" }
        val host = requireNotNull(extras.getString("host")) { "Argument 'host' is missing" }

        val repo = get<PreferredAppRepository>()

        runBlocking { repo.deleteByHost(host) }

        Toast.makeText(context, "Deleted preferred app for $host", Toast.LENGTH_SHORT).show()
    }
}
