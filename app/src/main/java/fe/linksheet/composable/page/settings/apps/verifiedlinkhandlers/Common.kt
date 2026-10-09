package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import app.linksheet.feature.app.core.LinkHandling

fun createHostState(
    hostSet: Set<String>,
    linkHandling: LinkHandling,
    preferredHosts: Set<String>
): SnapshotStateMap<String, Boolean> {
    val map = mutableStateMapOf<String, Boolean>()
    when (linkHandling) {
        LinkHandling.Unsupported, LinkHandling.Browser -> for (host in preferredHosts) {
            map[host] = true
        }

        else -> for (host in hostSet) {
            map[host] = host in preferredHosts
        }
    }

    return map
}
