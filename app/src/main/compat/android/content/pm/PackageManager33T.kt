package android.content.pm

import android.content.Intent
import fe.linksheet.util.AndroidVersion

fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: Int = 0): MutableList<ResolveInfo> {
    return if (AndroidVersion.AT_LEAST_API_33_T) queryIntentActivities(
        intent, PackageManager.ResolveInfoFlags.of(flags.toLong())
    )
    else queryIntentActivities(intent, flags)
}


fun PackageManager.hasLauncher(packageName: String?): ResolveInfo? {
    if(packageName == null) return null

    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(packageName)
    return queryIntentActivitiesCompat(intent).singleOrNull()
}

fun PackageManager.getAppsWithLauncher(): List<ResolveInfo> {
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return queryIntentActivitiesCompat(intent)
}


