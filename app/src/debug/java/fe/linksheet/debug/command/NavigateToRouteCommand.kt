package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import fe.linksheet.LinkSheetApp
import fe.linksheet.debug.DebugBroadcastReceiver
import org.koin.core.component.get


object NavigateToRouteCommand : DebugCommand<NavigateToRouteCommand>(
    DebugBroadcastReceiver.NAVIGATE_BROADCAST, NavigateToRouteCommand::class
) {

    override fun handle(context: Context, intent: Intent) {
        val extras = requireNotNull(intent.extras) { "Extras must not be null" }
        val route = requireNotNull(extras.getString("route")) { "Argument 'route' is missing" }

        get<LinkSheetApp>().activityEventListener?.invoke(route)
    }
}
