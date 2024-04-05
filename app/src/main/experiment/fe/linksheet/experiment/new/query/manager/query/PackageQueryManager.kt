package fe.linksheet.experiment.new.query.manager.query

import android.R.attr.host
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.resolver.UriViewActivity
import fe.linksheet.util.AndroidVersion
import org.koin.core.component.KoinComponent


object PackageQueryManager : KoinComponent {
    private val logger by injectLogger<PackageQueryManager>()

    @RequiresApi(Build.VERSION_CODES.S)
    fun findHandlers(context: Context, uri: Uri): List<UriViewActivity> {
        val dvm = context.getSystemService<DomainVerificationManager>()!!
        val host = uri.host.toString()

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val viewIntent = Intent(Intent.ACTION_VIEW, uri)

        val packageManager = context.packageManager
        val result = packageManager.queryIntentActivitiesCompat(mainIntent, PackageManager.MATCH_ALL)
        return result
            .filter { it.canHandle(dvm, host) }
            .map { it.toUriHandler(packageManager, viewIntent) }
            .toList()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun ResolveInfo.canHandle(dvm: DomainVerificationManager, host: String): Boolean {
        // TODO: Does this work for wildcard subdomains? (*.example.org?)
        val hostToStateMap = dvm.getDomainVerificationUserState(activityInfo.packageName)?.hostToStateMap
        return hostToStateMap?.contains(host) == true || matchesWildcard(hostToStateMap, host)
    }

    private fun matchesWildcard(hostToStateMap: Map<String, Int>?, host: String): Boolean {
        if (hostToStateMap == null) return false

        // https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/pm/verify/domain/DomainVerificationService.java;l=1918
        for ((domain, _) in hostToStateMap) {
            if (domain.startsWith("*.") && host.endsWith(domain.substring(2))) {
                return true
            }
        }

        return false
    }

    private fun ResolveInfo.toUriHandler(packageManager: PackageManager, viewIntent: Intent): UriViewActivity {
//        val activities = packageManager.getPackageInfo(
//            activityInfo.packageName,
//            PackageManager.MATCH_ALL or PackageManager.GET_ACTIVITIES or PackageManager.GET_RESOLVED_FILTER
//        ).activities.filter { it.exported }

        val mainIntent = Intent(Intent.ACTION_MAIN)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        mainIntent.selector = viewIntent
            .setPackage(activityInfo.packageName)
//            .addCategory(Intent.CATEGORY_DEFAULT)
            .addCategory(Intent.CATEGORY_BROWSABLE)

        try {
            val resolved = packageManager.queryIntentActivitiesCompat(
                mainIntent, PackageManager.MATCH_ALL or PackageManager.GET_RESOLVED_FILTER
            )

            if (resolved.isNotEmpty()) {
                return UriViewActivity(resolved.first(), false)
            }
        } catch (e: Throwable) {
            logger.error(e)
        }

//        val ac = activities
//        for (activity in activities) {
//            val componentName = ComponentName(activity.packageName, activity.name)
//            val activityIntent = viewIntent.addCategory(Intent.CATEGORY_BROWSABLE).setComponent(componentName)
//            val resolved = packageManager.queryIntentActivitiesCompat(
//                activityIntent, PackageManager.MATCH_ALL or PackageManager.GET_RESOLVED_FILTER
//            )
//
//            val size = resolved.size
//        }
//
////        packageManager.getLaunchIntentForPackage()

        return UriViewActivity(this, true)


//
//
//        val test = packageManager.queryIntentActivitiesCompat(
//            Intent(Intent.ACTION_VIEW, null).setPackage(activityInfo.packageName).addCategory(Intent.CATEGORY_DEFAULT)
//                .addCategory(Intent.CATEGORY_BROWSABLE), PackageManager.MATCH_ALL or PackageManager.GET_RESOLVED_FILTER
//        )
//
//        val alternative = packageManager.resolveActivity(
//            Intent(viewIntent).addCategory(Intent.CATEGORY_BROWSABLE).setPackage(activityInfo.packageName),
//            PackageManager.MATCH_ALL
//        )
//
//        return packageManager.resolveActivity(
//            Intent(viewIntent).addCategory(Intent.CATEGORY_BROWSABLE).setPackage(activityInfo.packageName),
//            PackageManager.MATCH_ALL
//        )
    }

    private fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: Int): MutableList<ResolveInfo> {
        return if (AndroidVersion.AT_LEAST_API_33_T) {
            queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
        } else queryIntentActivities(intent, flags)
    }
}
