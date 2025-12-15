package app.linksheet.feature.app.core

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.annotation.VisibleForTesting
import app.linksheet.feature.app.extension.activityDescriptor
import fe.composekit.extension.packageName
import fe.linksheet.util.ResolveInfoFlags

interface PackageIntentHandler {
    fun isSelfDefaultBrowser(): Boolean
    fun findHttpBrowsable(packageName: String?): List<ResolveInfo>
    fun findSupportedHosts(packageName: String): Set<String>
    fun findHandlers(intent: Intent): List<ResolveInfo>
    fun findHandlers(uri: Uri, referringPackage: String?): List<ResolveInfo>
    fun isLinkHandler(filter: IntentFilter, uri: Uri): Boolean
}

class DefaultPackageIntentHandler(
    private val queryIntentActivities: (Intent, ResolveInfoFlags) -> List<ResolveInfo>,
    private val resolveActivity: (Intent, ResolveInfoFlags) -> ResolveInfo?,
    private val isLinkSheetCompat: (String) -> Boolean,
    private val isSelf: (String) -> Boolean,
) : PackageIntentHandler {

    companion object {
        private val QUERY_FLAGS = ResolveInfoFlags.select(
            ResolveInfoFlags.MATCH_ALL,
            ResolveInfoFlags.GET_RESOLVED_FILTER,
            ResolveInfoFlags.GET_META_DATA
        )

        private val anyHost = IntentFilter.AuthorityEntry("*", "-1")

        private val httpSchemeUri: Uri = Uri.fromParts("http", "", "")
        private val httpsSchemeUri: Uri = Uri.fromParts("https", "", "")
    }

    override fun isSelfDefaultBrowser(): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, httpSchemeUri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
        val resolveInfo = resolveActivity(intent, ResolveInfoFlags.MATCH_DEFAULT_ONLY)
        val pkg = resolveInfo?.packageName ?: return false
        return isSelf(pkg)
    }

    internal fun ResolveInfo.isLaunchable(): Boolean {
        return activityInfo.exported && activityInfo.applicationInfo.enabled
    }

    override fun findHttpBrowsable(packageName: String?): List<ResolveInfo> {
        val httpIntent = Intent(Intent.ACTION_VIEW, httpSchemeUri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setPackage(packageName)

        val httpInfos = queryIntentActivities(httpIntent, ResolveInfoFlags.MATCH_ALL)
        val httpsInfos = queryIntentActivities(httpIntent.setData(httpsSchemeUri), ResolveInfoFlags.MATCH_ALL)
        return (httpInfos + httpsInfos)
            .distinctBy { it.activityInfo.activityDescriptor }
            .filter { it.isLaunchable() }
            .filter { !isSelf(it.packageName) }
    }

    override fun findSupportedHosts(packageName: String): Set<String> {
        val httpIntent = Intent(Intent.ACTION_VIEW, httpSchemeUri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setPackage(packageName)

        val httpInfos = queryIntentActivities(httpIntent, ResolveInfoFlags.MATCH_ALL)
        val httpsInfos = queryIntentActivities(httpIntent.setData(httpsSchemeUri), ResolveInfoFlags.MATCH_ALL)

        return (httpInfos + httpsInfos)
            .filter { it.filter != null }
            .flatMapTo(HashSet()) { it.filter.getHosts() }
    }

    override fun findHandlers(intent: Intent): List<ResolveInfo> {
        val activities = queryIntentActivities(intent, QUERY_FLAGS)
        return activities
            .filter { it.isLaunchable() }
            .filter { !isLinkSheetCompat(it.packageName) }
    }

    override fun findHandlers(uri: Uri, referringPackage: String?): List<ResolveInfo> {
        val viewIntent = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
        val activities = findHandlers(viewIntent)

        return activities.filter { isLinkHandler(it.filter, uri) }
    }

    @VisibleForTesting
    override fun isLinkHandler(filter: IntentFilter, uri: Uri): Boolean {
        return filter.hasNonWildcardDataAuthority(uri) || filter.hasDataPath(uri.path)
    }

    private fun IntentFilter.hasNonWildcardDataAuthority(uri: Uri): Boolean {
        return getDataAuthorities().any { it != anyHost && it.match(uri) >= 0 }
    }

    private fun IntentFilter.getDataAuthorities(): List<IntentFilter.AuthorityEntry> {
        return (0 until countDataAuthorities()).map { getDataAuthority(it) }
    }

    private fun IntentFilter.getHosts(): List<String> {
        return getDataAuthorities()
            .filter { it != anyHost }
            .mapNotNull { it.host }
    }
}
