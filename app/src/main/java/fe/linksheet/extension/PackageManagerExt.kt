package fe.linksheet.extension

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

fun PackageManager.queryFirstIntentActivityByPackageNameOrNull(packageName: String): ResolveInfo? {
    val intent = Intent().setPackage(packageName)
    return queryIntentActivitiesByIntent(intent.addCategory(Intent.CATEGORY_LAUNCHER)).firstOrNull() ?: queryIntentActivitiesByIntent(intent).firstOrNull()
}


fun PackageManager.queryFirstIntentActivityByComponentNameOrNull(componentName: ComponentName) =
    queryIntentActivitiesByIntent(Intent().setComponent(componentName)).firstOrNull()

fun PackageManager.queryIntentActivitiesByIntent(intent: Intent) =
    queryIntentActivities(intent, PackageManager.MATCH_ALL)