package app.linksheet.testing

import app.linksheet.testing.util.PackageInfoFake
import app.linksheet.testing.util.firstActivityResolveInfo
import fe.composekit.extension.componentName
import fe.linksheet.module.database.entity.PreferredApp

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
