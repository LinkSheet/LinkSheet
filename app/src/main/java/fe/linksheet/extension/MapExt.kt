package fe.linksheet.extension

import com.tasomaniac.openwith.resolver.DisplayActivityInfo

private val comparator = compareByDescending<Pair<DisplayActivityInfo, Boolean>> {
        (_, bool) -> bool
}.thenBy { (key, _) -> key }

fun Map<DisplayActivityInfo, Boolean>.sortByValueAndName() = this.toList().sortedWith(comparator)