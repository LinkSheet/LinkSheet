package app.linksheet.testing

import app.linksheet.testing.util.PackageInfoFake
import app.linksheet.testing.util.firstActivityResolveInfo
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.util.extension.android.componentName

fun PackageInfoFake.asPreferredApp(host: String, alwaysPreferred: Boolean = false): PreferredApp {
    val resolveInfo = firstActivityResolveInfo
    val componentName = resolveInfo?.activityInfo?.componentName

    return PreferredApp(
        _packageName = componentName?.packageName,
        _component = componentName?.flattenToString(),
        host = host,
        alwaysPreferred = alwaysPreferred
    )
}
