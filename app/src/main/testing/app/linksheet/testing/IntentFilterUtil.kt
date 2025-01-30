package app.linksheet.testing

import android.content.IntentFilter


fun buildIntentFilter(block: IntentFilter.() -> Unit = {}): IntentFilter {
    return IntentFilter().apply(block)
}

fun IntentFilter.addDataPaths(type: Int, vararg paths: String) {
    for (path in paths) {
        addDataPath(path, type)
    }
}

fun IntentFilter.addDataTypes(vararg types: String) {
    for (type in types) {
        addDataType(type)
    }
}

fun IntentFilter.addHosts(vararg hosts: String) {
    for (host in hosts) {
        addDataAuthority(host, null)
    }
}
