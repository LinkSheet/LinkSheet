package com.tasomaniac.openwith.preferred

import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.resolver.DisplayActivityInfo

data class PreferredDisplayActivityInfo(
    val app: PreferredApp,
    val displayActivityInfo: DisplayActivityInfo
)
