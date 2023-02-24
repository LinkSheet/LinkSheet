package fe.linksheet.extension

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager

fun PackageManager.queryFirstIntentActivityByPackageNameOrNull(packageName: String) =
    queryFirstIntentActivitiesByIntent(Intent().setPackage(packageName))

fun PackageManager.queryFirstIntentActivityByComponentNameOrNull(componentName: ComponentName) =
    queryFirstIntentActivitiesByIntent(Intent().setComponent(componentName))

fun PackageManager.queryFirstIntentActivitiesByIntent(intent: Intent) =
    queryIntentActivities(intent, PackageManager.MATCH_ALL).firstOrNull()