package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import fe.linksheet.LinkSheetApp
import fe.linksheet.activity.util.UiEvent
import fe.linksheet.activity.util.UiEventReceiver
import fe.linksheet.debug.DebugBroadcastReceiver
import org.koin.core.component.inject


object NavigateToRouteCommand : DebugCommand<NavigateToRouteCommand>(
    DebugBroadcastReceiver.NAVIGATE_BROADCAST, NavigateToRouteCommand::class
) {
    private val app by inject<LinkSheetApp>()

    override fun handle(context: Context, intent: Intent) {
        val extras = requireNotNull(intent.extras) { "Extras must not be null" }
        val route = requireNotNull(extras.getString("route")) { "Argument 'route' is missing" }

        val activity = app.currentActivity().value
        if (activity != null && activity is UiEventReceiver) {
            activity.send(UiEvent.NavigateTo(route = route))
        }
    }
}
