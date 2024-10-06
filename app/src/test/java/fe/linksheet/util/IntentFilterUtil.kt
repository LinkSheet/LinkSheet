package fe.linksheet.util

import android.content.IntentFilter


fun buildIntentFilter(block: IntentFilter.() -> Unit = {}): IntentFilter {
    return IntentFilter().apply(block)
}

fun IntentFilter.addDataPaths(type: Int, vararg paths: String) {
    for (path in paths) {
        addDataPath(path, type)
    }
}
