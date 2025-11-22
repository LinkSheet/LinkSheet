package fe.linksheet.module.repository.whitelisted

import android.content.ComponentName


data class WhitelistedBrowserInfo(val cmps: Set<ComponentName>, val pkgs: Set<String>)

fun createWhitelistedBrowserInfo(list: Iterable<String>): WhitelistedBrowserInfo {
    val cmps = mutableSetOf<ComponentName>()
    val pkgs = mutableSetOf<String>()
    for (item in list) {
        val cmp = ComponentName.unflattenFromString(item)
        if (cmp != null) {
            cmps.add(cmp)
        } else {
            pkgs.add(item)
        }
    }

    return WhitelistedBrowserInfo(cmps, pkgs)
}
