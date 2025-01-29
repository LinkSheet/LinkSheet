package fe.linksheet.module.app.`package`

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.queryIntentActivitiesCompat
import android.net.Uri
import androidx.annotation.VisibleForTesting
import fe.linksheet.lib.flavors.LinkSheetApp.Compat
import fe.linksheet.util.ResolveInfoFlags

internal fun AndroidPackageIntentHandler(
    packageManager: PackageManager,
    checkReferrerExperiment: () -> Boolean,
    checkDisableDeduplicationExperiment: () -> Boolean = { false },
): PackageIntentHandler {
    return DefaultPackageIntentHandler(
        queryIntentActivities = packageManager::queryIntentActivitiesCompat,
        isLinkSheetCompat = { pkg -> Compat.isApp(pkg) != null },
        checkReferrerExperiment = checkReferrerExperiment,
        checkDisableDeduplicationExperiment = checkDisableDeduplicationExperiment
    )
}

interface PackageIntentHandler {
    fun findHandlers(intent: Intent): List<ResolveInfo>
    fun findHandlers(uri: Uri, referringPackage: String?): List<ResolveInfo>
    fun isLinkHandler(filter: IntentFilter, uri: Uri): Boolean
}

internal class DefaultPackageIntentHandler(
    val queryIntentActivities: (Intent, ResolveInfoFlags) -> List<ResolveInfo>,
    val isLinkSheetCompat: (String) -> Boolean,
    val checkReferrerExperiment: () -> Boolean,
    val checkDisableDeduplicationExperiment: () -> Boolean = { false },
) : PackageIntentHandler {

    companion object {
        private val QUERY_FLAGS = ResolveInfoFlags.select(
            ResolveInfoFlags.MATCH_ALL,
            ResolveInfoFlags.GET_RESOLVED_FILTER,
            ResolveInfoFlags.GET_META_DATA
        )

        private val anyHost = IntentFilter.AuthorityEntry("*", "-1")
    }

    class ActivityAlias(
        var activity: ResolveInfo? = null,
        private val aliases: MutableList<ResolveInfo> = mutableListOf(),
    ) {
        fun add(alias: ResolveInfo) {
            aliases.add(alias)
        }

        fun getBestActivity(): ResolveInfo? {
            if (activity?.activityInfo?.enabled == true) return activity!!
            return aliases.firstOrNull { it.activityInfo.enabled } ?: activity ?: aliases.firstOrNull()
        }
    }

    override fun findHandlers(intent: Intent): List<ResolveInfo> {
        val activities = queryIntentActivities(intent, QUERY_FLAGS)
        return activities.filter { it.activityInfo.applicationInfo.enabled && !isLinkSheetCompat(it.activityInfo.packageName) }
    }

    override fun findHandlers(uri: Uri, referringPackage: String?): List<ResolveInfo> {
        val viewIntent = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
        val activities = findHandlers(viewIntent)

        var filtered = activities.filter { isLinkHandler(it.filter, uri) }

        if (referringPackage != null && checkReferrerExperiment()) {
            filtered = filtered.filter { it.activityInfo.packageName != referringPackage }
        }

        if (checkDisableDeduplicationExperiment()) {
            return filtered
        }

        return deduplicate(filtered)
    }

    private fun deduplicate(filtered: List<ResolveInfo>): List<ResolveInfo> {
        val map = mutableMapOf<String, ActivityAlias>()
        for (activity in filtered) {
            val target = activity.activityInfo?.targetActivity
            val key = "${activity.activityInfo.packageName}/${target ?: activity.activityInfo.name}"

            val activityAlias = map.getOrPut(key) { ActivityAlias() }
            if (target == null) {
                activityAlias.activity = activity
            } else {
                activityAlias.add(activity)
            }
        }

        return map.mapNotNull { (_, activity) -> activity.getBestActivity() }
    }

    @VisibleForTesting
    override fun isLinkHandler(filter: IntentFilter, uri: Uri): Boolean {
        val authorityCount = filter.countDataAuthorities().takeIf { it > 0 } ?: return false
        return filter.hasNonWildcardDataAuthority(authorityCount, uri) || filter.hasDataPath(uri.path)
    }

    private fun IntentFilter.hasNonWildcardDataAuthority(size: Int, uri: Uri): Boolean {
        return (0 until size).map { getDataAuthority(it) }.any { it != anyHost && it.match(uri) >= 0 }
    }
}
