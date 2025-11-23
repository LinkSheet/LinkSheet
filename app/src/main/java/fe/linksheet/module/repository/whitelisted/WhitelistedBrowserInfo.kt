package fe.linksheet.module.repository.whitelisted

import android.content.ComponentName


data class WhitelistedBrowserInfo(
    val componentNames: Set<ComponentName>,
    val packageNames: Set<String>
)

fun createWhitelistedBrowserInfo(list: Iterable<String>): WhitelistedBrowserInfo {
    val componentNames = mutableSetOf<ComponentName>()
    val packages = mutableSetOf<String>()
    for (item in list) {
        val cmp = ComponentName.unflattenFromString(item)
        if (cmp != null) {
            componentNames.add(cmp)
        } else {
            packages.add(item)
        }
    }

    return WhitelistedBrowserInfo(componentNames, packages)
}
