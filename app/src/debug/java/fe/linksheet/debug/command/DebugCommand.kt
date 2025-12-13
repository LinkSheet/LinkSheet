package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import mozilla.components.support.base.log.logger.Logger
import org.koin.core.component.KoinComponent
import kotlin.reflect.KClass

abstract class DebugCommand<T : DebugCommand<T>>(val action: String, command: KClass<in T>) : KoinComponent {
    protected val logger = Logger(command.simpleName)

    abstract fun handle(context: Context, intent: Intent)

    companion object {
        private val commands = setOf(
            UpdatePreferenceCommand,
            NavigateToRouteCommand,
            ResetHistoryPreferredAppCommand,
            DumpPreferencesCommand,
            ViewUrlCommand,
            DumpNavGraphCommand,
            ImportPreferencesCommand,
        ).associateBy { it.action }

        fun tryHandle(context: Context, intent: Intent): Boolean {
            val command = commands[intent.action] ?: return false
            runCatching { command.handle(context, intent) }.onFailure { command.logger.error("Command failed", it) }

            return true
        }
    }
}
