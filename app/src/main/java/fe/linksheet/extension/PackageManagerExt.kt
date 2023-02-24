package fe.linksheet.extension

import android.content.Intent
import android.content.pm.PackageManager

fun PackageManager.queryFirstIntentActivityByPackageNameOrNull(packageName: String) =
    queryIntentActivities(Intent().setPackage(packageName), PackageManager.MATCH_ALL).firstOrNull()