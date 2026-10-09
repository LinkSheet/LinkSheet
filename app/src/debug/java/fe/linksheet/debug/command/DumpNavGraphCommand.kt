package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import fe.linksheet.LinkSheetApp
import fe.linksheet.activity.util.NavGraphDebugState
import fe.linksheet.activity.util.UiEventReceiver
import fe.linksheet.debug.DebugBroadcastReceiver
import org.koin.core.component.inject

object DumpNavGraphCommand : DebugCommand<DumpNavGraphCommand>(
    DebugBroadcastReceiver.DUMP_NAV_GRAPH_BROADCAST, DumpNavGraphCommand::class
) {
    private val app by inject<LinkSheetApp>()

    override fun handle(context: Context, intent: Intent) {
        val activity = app.currentActivity().value
        if (activity == null || activity !is UiEventReceiver) return
        val navGraph = activity.getStateOrNull<NavGraphDebugState>(NavGraphDebugState.Key) ?: return
        val routes = navGraph.graphNodes.map { it.route }.sortedBy { it }

        for (route in routes) {
            logger.info("\t$route")
        }
    }
}
