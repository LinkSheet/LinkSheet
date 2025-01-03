package fe.linksheet.module.app

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.annotation.VisibleForTesting
import fe.linksheet.util.ResolveInfoFlags

class PackageIntentHandler(
    val queryIntentActivities: (Intent, ResolveInfoFlags) -> List<ResolveInfo>,
    val isLinkSheetCompat: (String) -> Boolean,
    val checkReferrerExperiment: () -> Boolean,
) {
    companion object {
        private val QUERY_FLAGS = ResolveInfoFlags.select(
            ResolveInfoFlags.MATCH_ALL,
            ResolveInfoFlags.GET_RESOLVED_FILTER,
            ResolveInfoFlags.GET_META_DATA
        )
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

    fun findHandlers(intent: Intent): List<ResolveInfo> {
        val activities = queryIntentActivities(intent, QUERY_FLAGS)
        return activities.filter { it.activityInfo.applicationInfo.enabled && !isLinkSheetCompat(it.activityInfo.packageName) }
    }

    fun findHandlers(uri: Uri, referringPackage: String?): List<ResolveInfo> {
        val viewIntent = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
        val activities = findHandlers(viewIntent)

        var filtered = activities.filter { isLinkHandler(it.filter, uri) }

        if (referringPackage != null && checkReferrerExperiment()) {
            filtered = filtered.filter { it.activityInfo.packageName != referringPackage }
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
    fun isLinkHandler(filter: IntentFilter, uri: Uri): Boolean {
        val authorityCount = filter.countDataAuthorities().takeIf { it > 0 } ?: return false
        return filter.hasNonWildcardDataAuthority(authorityCount, uri) || filter.hasDataPath(uri.path)
    }

    private val anyHost = IntentFilter.AuthorityEntry("*", "-1")

    private fun IntentFilter.hasNonWildcardDataAuthority(size: Int, uri: Uri): Boolean {
        return (0 until size).map { getDataAuthority(it) }.any { it != anyHost && it.match(uri) >= 0 }
    }
}
