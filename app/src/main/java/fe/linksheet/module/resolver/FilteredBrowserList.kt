package fe.linksheet.module.resolver

import android.content.pm.ResolveInfo
import fe.linksheet.module.resolver.browser.BrowserMode

data class FilteredBrowserList(
    val browserMode: BrowserMode,
    val browsers: List<ResolveInfo>,
    val apps: List<ResolveInfo>,
    val isSingleOption: Boolean = false,
    val noBrowsersOnlySingleApp: Boolean = false,
)
