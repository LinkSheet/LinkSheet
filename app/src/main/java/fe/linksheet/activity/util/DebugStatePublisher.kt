package fe.linksheet.activity

import androidx.navigation.NavDestination
import kotlin.reflect.KClass

object DebugStatePublisher {
    val map = mutableMapOf<KClass<out DebugState>, DebugState>()

    fun <T : DebugState> publishDebugState(state: T) {
        map[state::class] = state
        val x = map
    }

    fun <T : DebugState> getStateOrNull(clazz: KClass<out T>): T? {
        @Suppress("UNCHECKED_CAST")
        return map[clazz] as T?
    }

    inline fun <reified T : DebugState> getStateOrNull(): T? {
        return getStateOrNull(T::class)
    }
}

sealed interface DebugState

data class NavGraphDebugState(val graphNodes: List<NavDestination>) : DebugState
