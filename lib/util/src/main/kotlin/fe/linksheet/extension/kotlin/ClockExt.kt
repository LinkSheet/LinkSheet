@file:OptIn(ExperimentalTime::class)

package fe.linksheet.extension.kotlin

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun Clock.nowMillis(): Long {
    return now().toEpochMilliseconds()
}
