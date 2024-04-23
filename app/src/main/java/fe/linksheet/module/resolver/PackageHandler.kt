package fe.linksheet.module.resolver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentFilter.AuthorityEntry
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.queryIntentActivitiesCompat
import android.net.Uri
import fe.linksheet.util.BitFlagUtil
import fe.linksheet.util.LinkSheetCompat


object PackageHandler {
    private val QUERY_FLAGS = BitFlagUtil.or(
        PackageManager.MATCH_ALL,
        PackageManager.GET_RESOLVED_FILTER,
        PackageManager.MATCH_DISABLED_COMPONENTS,
        PackageManager.GET_META_DATA
    )

    class ActivityAlias(
        var activity: ResolveInfo? = null,
        private val aliases: MutableList<ResolveInfo> = mutableListOf(),
    ) {
        fun add(alias: ResolveInfo) {
            aliases.add(alias)
        }

        fun get(): ResolveInfo? {
            if (activity?.activityInfo?.enabled == true) return activity!!
            return aliases.firstOrNull { it.activityInfo.enabled } ?: aliases.firstOrNull()
        }
    }

    fun findHandlers(context: Context, uri: Uri): List<ResolveInfo> {
        val viewIntent = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
        val activities = context.packageManager.queryIntentActivitiesCompat(viewIntent, QUERY_FLAGS)

        val filtered = activities.filter {
            it.activityInfo.applicationInfo.enabled && !LinkSheetCompat.isCompat(it) && isLinkHandler(it.filter, uri)
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

        return map.mapNotNull { (_, activity) -> activity.get() }
    }

    fun isLinkHandler(filter: IntentFilter, uri: Uri): Boolean {
        val authorityCount = filter.countDataAuthorities().takeIf { it > 0 } ?: return false
        return filter.hasNonWildcardDataAuthority(authorityCount, uri) || filter.hasDataPath(uri.path)
    }

    private val anyHost = AuthorityEntry("*", "-1")

    private fun IntentFilter.hasNonWildcardDataAuthority(size: Int, uri: Uri): Boolean {
        return (0 until size).map { getDataAuthority(it) }.any { it != anyHost && it.match(uri) >= 0 }
    }
}
