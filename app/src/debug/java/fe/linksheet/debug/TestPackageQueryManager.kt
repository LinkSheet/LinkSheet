package fe.linksheet.debug

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.composekit.core.AndroidVersion
import mozilla.components.support.base.log.logger.Logger
import org.koin.core.component.KoinComponent


object TestPackageQueryManager : KoinComponent {
    private val logger = Logger("TestPackageQueryManager")

    @RequiresApi(Build.VERSION_CODES.S)
    fun findHandlers(context: Context, uri: Uri) {
        val dvm = context.getSystemService<DomainVerificationManager>()!!
        val host = uri.host.toString()

//        val mainIntent = Intent(Intent.ACTION_MAIN, null)
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//        val mainIntent = Intent(Intent.ACTION_MAIN, null)
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//        val mainIntent = Intent(Intent.ACTION_MAIN)
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//        mainIntent.selector = Intent(Intent.ACTION_VIEW, uri)
//                        .setComponent(ComponentName(activity.packageName, activity.name))
//            .addCategory(Intent.CATEGORY_DEFAULT)
//            .addCategory(Intent.CATEGORY_BROWSABLE)
//
        val viewIntent = Intent(Intent.ACTION_VIEW, uri)
            .addCategory(Intent.CATEGORY_BROWSABLE)

        val activities = context.packageManager.queryIntentActivitiesCompat(
            viewIntent,
            PackageManager.MATCH_ALL or PackageManager.GET_RESOLVED_FILTER
//                    or PackageManager.MATCH_DISABLED_COMPONENTS
        )

//
//        val resolve = activities.first()
//        resolve.activityInfo.componentName()
//
//
//        return activities.map { UriViewActivity(it, false) }

//        val packageManager = context.packageManager
//        val result = packageManager.queryIntentActivitiesCompat(mainIntent, PackageManager.MATCH_ALL)
//        return result
//            .filter { it.canHandle(dvm, host) }
//            .map { it.toUriHandler(packageManager, viewIntent) }
//            .toList()
    }
//    com.amazon.mShop.android.beta

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

    private val getAllIntentFilters by lazy {
        PackageManager::class.java.getMethod("getAllIntentFilters", String::class.java)
    }

    private fun PackageManager.getAllIntentFilters(packageName: String): List<IntentFilter> {
        @Suppress("UNCHECKED_CAST")
        return getAllIntentFilters.invoke(this, packageName) as List<IntentFilter>
    }

    private fun ResolveInfo.toUriHandler(packageManager: PackageManager, viewIntent: Intent) {
        val activities = packageManager.getPackageInfo(
            activityInfo.packageName,
            PackageManager.MATCH_ALL or PackageManager.GET_ACTIVITIES or PackageManager.GET_RESOLVED_FILTER or PackageManager.MATCH_DISABLED_COMPONENTS
        ).activities?.filter { it.exported } ?: emptyList()

        val resolved = packageManager.resolveActivity(
            Intent(viewIntent).addCategory(Intent.CATEGORY_DEFAULT)
                .addCategory(Intent.CATEGORY_BROWSABLE).setPackage(activityInfo.packageName),
            PackageManager.GET_RESOLVED_FILTER or PackageManager.MATCH_DISABLED_COMPONENTS
        )

        try {
            val intentFilters = packageManager.getAllIntentFilters(activityInfo.packageName)

            val matches = mutableListOf<Pair<IntentFilter, Int>>()
            for (filter in intentFilters) {
                val match = filter.match(
                    viewIntent.action,
                    null,
                    viewIntent.scheme,
                    viewIntent.data,
                    viewIntent.categories,
                    "Test"
                )

                if (match > 0) {
                    val lol = filter
                    matches.add(filter to match)
                    Log.d("Match", "$match")
                }
            }

            if (matches.isNotEmpty()) {
                val (filter, match) = matches.maxBy { (_, match) -> match }



                for (activity in activities) {
//                    val mainIntent = Intent(Intent.ACTION_MAIN)
//                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//                    mainIntent.selector = Intent(viewIntent)
//                        .setComponent(ComponentName(activity.packageName, activity.name))
//                        .addCategory(Intent.CATEGORY_DEFAULT)
//                        .addCategory(Intent.CATEGORY_BROWSABLE)
                    val intent2 = Intent(viewIntent).addCategory(Intent.CATEGORY_DEFAULT)
                        .addCategory(Intent.CATEGORY_BROWSABLE)
                        .setComponent(ComponentName(activity.packageName, activity.name))

                    val resolved = packageManager.resolveActivity(
                        Intent(viewIntent).addCategory(Intent.CATEGORY_DEFAULT)
                            .addCategory(Intent.CATEGORY_BROWSABLE).setPackage(activity.packageName),
//                            .setComponent(ComponentName(activity.packageName, activity.name)),
                        PackageManager.GET_RESOLVED_FILTER or PackageManager.MATCH_DISABLED_COMPONENTS
                    )

//                    val resolved = packageManager.queryIntentActivitiesCompat(
//                        mainIntent, PackageManager.MATCH_ALL or PackageManager.GET_RESOLVED_FILTER
//                    )
//                    val resolved = intent2.resolveActivityInfo(packageManager,  PackageManager.GET_SHARED_LIBRARY_FILES)
//                    val current = ActivityThread.currentActivityThread()
//                    ActivityManagerCompat.isUserAMonkey()


                    val test = resolved
//                    if(test > 0){
//                        val hehe = resolved
//                    }

//                    if(resolved.isNotEmpty()){
//                        UriViewActivity(resolved.first(), false)
//                    }
                }

//                val activity = activities.firstOrNull { it.flags == match }
//                if (activity != null) {
//                    val mainIntent = Intent(Intent.ACTION_MAIN)
//                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//                    mainIntent.selector = Intent(viewIntent)
//                        .setComponent(ComponentName(activity.packageName, activity.name))
//                        .addCategory(Intent.CATEGORY_DEFAULT)
//                        .addCategory(Intent.CATEGORY_BROWSABLE)
//
//                    val resolved = packageManager.queryIntentActivitiesCompat(
//                        mainIntent, PackageManager.MATCH_ALL or PackageManager.GET_RESOLVED_FILTER
//                    )
//
//                    if(resolved.isNotEmpty()){
//                        UriViewActivity(resolved.first(), false)
//                    }
//                }
//                val mainIntent = Intent(Intent.ACTION_MAIN)
//                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//                mainIntent.selector = viewIntent
//                    .setPackage(activityInfo.packageName)
//                    .addCategory(Intent.CATEGORY_DEFAULT)
//                    .addCategory(Intent.CATEGORY_BROWSABLE)
//                for (activity in activities) {
//                    val componentName = ComponentName(activity.packageName, activity.name)
//            val activityIntent = mainIntent
//                    val activityIntent = Intent(Intent.ACTION_MAIN)
//                    activityIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//                    activityIntent.selector = viewIntent
//                        .setComponent(componentName)
//                        .addCategory(Intent.CATEGORY_DEFAULT)
//                        .addCategory(Intent.CATEGORY_BROWSABLE)
//
////            val activityIntent = viewIntent.addCategory(Intent.CATEGORY_BROWSABLE).setComponent(componentName)
//                    val resolved = packageManager.queryIntentActivitiesCompat(
//                        activityIntent, PackageManager.MATCH_ALL  or PackageManager.GET_RESOLVED_FILTER
//                    )
//
//                    val size = resolved.size
//                    val match = resolved.firstOrNull()?.match ?: -1
//                    val filter = resolved.firstOrNull()?.filter
//                    if (match > 0) {
//                        Log.d("PackageQueryManager", "match=$match")
//                    }
//                }


//                val resolved = packageManager.queryIntentActivitiesCompat(
//                    mainIntent, PackageManager.MATCH_ALL or PackageManager.GET_RESOLVED_FILTER
//                )

//                if (resolved.isNotEmpty()) {
//                    return UriViewActivity(resolved.first(), false)
//                }

//                return UriViewActivity(this, false)
            }
//            val filters = list
//            val appPkgManager = packageManager as ApplicationPackageManager

        } catch (e: Throwable) {
            e.printStackTrace()
        }

//
//        packageManager.getXml()
//        val newTest = viewIntent.setPackage(activityInfo.packageName)
//            .addCategory(Intent.CATEGORY_DEFAULT)
//            .addCategory(Intent.CATEGORY_BROWSABLE)
//        val resolved2 = packageManager.queryIntentActivitiesCompat(
//            newTest, PackageManager.MATCH_ALL or PackageManager.GET_RESOLVED_FILTER
//        )

//        try {
//            val resolved = packageManager.queryIntentActivitiesCompat(
//                mainIntent, PackageManager.MATCH_ALL or PackageManager.GET_RESOLVED_FILTER
//            )
//
//            if (resolved.isNotEmpty()) {
//                return UriViewActivity(resolved.first(), false)
//            }
//        } catch (e: Throwable) {
//            logger.error(e)
//        }


//


//        return UriViewActivity(this, true)


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
        return if (AndroidVersion.isAtLeastApi33T()) {
            queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
        } else queryIntentActivities(intent, flags)
    }
}
