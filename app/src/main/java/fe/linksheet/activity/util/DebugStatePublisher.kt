package fe.linksheet.activity.util

import androidx.navigation.NavDestination


abstract class DebugState(internal val key: Key<*>) {
    interface Key<SpecificParameter : DebugState>
}
data class NavGraphDebugState(
    val findDestination: (route: String) -> NavDestination?,
    val graphNodes: List<NavDestination>
) : DebugState(Key) {
    companion object {
        val Key = object : Key<NavGraphDebugState> {}
    }
}
