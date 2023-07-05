package fe.linksheet.extension.android

import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.applyIf

fun List<DisplayActivityInfo>.labelSorted(sorted: Boolean = true): List<DisplayActivityInfo> {
    return applyIf(sorted) { sortedWith(DisplayActivityInfo.labelComparator) }
}