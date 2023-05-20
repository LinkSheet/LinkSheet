package fe.linksheet.resolver

import fe.linksheet.module.database.entity.PreferredApp

data class PreferredDisplayActivityInfo(
    val app: PreferredApp,
    val displayActivityInfo: DisplayActivityInfo
)
