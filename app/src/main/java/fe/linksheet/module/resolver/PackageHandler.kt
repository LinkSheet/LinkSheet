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

    fun findHandlers(context: Context, uri: Uri): List<ResolveInfo> {
        val viewIntent = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
        val activities = context.packageManager.queryIntentActivitiesCompat(viewIntent, QUERY_FLAGS)

        return activities.filter {
            it.activityInfo.applicationInfo.enabled && !LinkSheetCompat.isCompat(it) && isLinkHandler(it.filter, uri)
        }
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
