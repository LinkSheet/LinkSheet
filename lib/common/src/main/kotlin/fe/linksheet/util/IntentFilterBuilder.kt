package fe.linksheet.util

import android.content.IntentFilter


fun buildIntentFilter(block: IntentFilter.() -> Unit): IntentFilter {
    val filter = IntentFilter()
    block(filter)

    return filter
}
