package fe.linksheet.extension.android

import fe.kotlin.util.applyIf
import fe.linksheet.resolver.DisplayActivityInfo

fun List<DisplayActivityInfo>.labelSorted(sorted: Boolean = true): List<DisplayActivityInfo> {
    return applyIf(sorted) { sortedWith(DisplayActivityInfo.labelComparator) }
}