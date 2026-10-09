package fe.linksheet.debug.command

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavDestination
import androidx.navigation.serialization.decodeArguments
import fe.composekit.bundle.buildBundle
import fe.composekit.bundle.put
import fe.linksheet.LinkSheetApp
import fe.linksheet.activity.util.NavGraphDebugState
import fe.linksheet.activity.util.UiEvent
import fe.linksheet.activity.util.UiEventReceiver
import fe.linksheet.debug.DebugBroadcastReceiver
import fe.std.result.isFailure
import fe.std.result.tryCatch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import org.koin.core.component.inject


object NavigateToRouteCommand : DebugCommand<NavigateToRouteCommand>(
    DebugBroadcastReceiver.NAVIGATE_BROADCAST, NavigateToRouteCommand::class
) {
    private val app by inject<LinkSheetApp>()

    @OptIn(InternalSerializationApi::class)
    @SuppressLint("RestrictedApi")
    override fun handle(context: Context, intent: Intent) {
        val extras = requireNotNull(intent.extras) { "Extras must not be null" }
        val routeName = requireNotNull(extras.getString("route")) { "Argument 'route' is missing" }

        val activity = app.currentActivity().value
        if (activity == null || activity !is UiEventReceiver) return
        val debugState = activity.getStateOrNull<NavGraphDebugState>(NavGraphDebugState.Key) ?: return
        val route = deserialize(debugState.findDestination, routeName, buildArgBundle(extras)) ?: return
        activity.onEvent(UiEvent.NavigateTo(route = route))
    }

    private fun buildArgBundle(extras: Bundle): Bundle {
        return buildBundle {
            for (key in extras.keySet()) {
                if (key.startsWith("_") && key.length > 1)  {
                    val value = extras.get(key)
                    put(key.substring(1), value)
                }
            }
        }
    }

    @OptIn(InternalSerializationApi::class)
    @SuppressLint("RestrictedApi")
    private fun deserialize(
        findDestination: (route: String) -> NavDestination?,
        route: String,
        argBundle: Bundle
    ): Any? {
        fun getClassNameFromDestination(destination: NavDestination): String {
            val route = destination.route!!
            val slashIdx = route.indexOf("/")
            val questionMarkIdx = route.indexOf("?")

            val cutOffIdx = slashIdx.coerceAtMost(questionMarkIdx)
            if (cutOffIdx != -1) {
                return route.substring(0, cutOffIdx)
            }

            return route
        }

        val destination = findDestination(route)
        if (destination == null || destination.route == null) {
            return null
        }
        val className = getClassNameFromDestination(destination)
        val classResult = tryCatch { Class.forName(className).kotlin }
        if (classResult.isFailure()) {
            logger.error("Unable to find class '$className'")
            return null
        }
        val kclass = classResult.value
        val arguments = destination.arguments
        for ((key, navArg) in arguments) {
            if (!argBundle.containsKey(key) && !navArg.isNullable && !navArg.isDefaultValuePresent) {
                logger.error("Argument '$key' is missing")
                return null
            }
        }
        val typeMap = arguments.mapValues { it.value.type }
        val decodeResult = tryCatch {
            kclass.serializer().decodeArguments(argBundle, typeMap)
        }
        if (decodeResult.isFailure()) {
            logger.error("Failed to decode '$kclass' with arguments '$argBundle'")
            return null
        }
        return decodeResult.value
    }
}
