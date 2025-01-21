package fe.linksheet.module.app

import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import fe.linksheet.util.ResolveInfoFlags

interface PackageInfoBrowserService {
    fun getBrowsableResolveInfos(packageName: String?): Set<ResolveInfo>?
}

class RealPackageInfoBrowserService(
    val queryIntentActivities: (Intent, ResolveInfoFlags) -> List<ResolveInfo>
) : PackageInfoBrowserService {

    companion object {
        private val httpSchemeUri: Uri = Uri.fromParts("http", "", "")
        private val httpsSchemeUri: Uri = Uri.fromParts("https", "", "")

        private val baseBrowserIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)

        val httpBrowserIntent = Intent(baseBrowserIntent).setData(httpSchemeUri)
        val httpsBrowserIntent = Intent(baseBrowserIntent).setData(httpsSchemeUri)
    }

    override fun getBrowsableResolveInfos(packageName: String?): Set<ResolveInfo>? {
        if (packageName == null) return null

        val http = Intent(httpBrowserIntent).setPackage(packageName)
        val https = Intent(httpsBrowserIntent).setPackage(packageName)

        val httpInfos = queryIntentActivities(http, ResolveInfoFlags.MATCH_ALL).toSet()
        val httpsInfos = queryIntentActivities(https, ResolveInfoFlags.MATCH_ALL).toSet()

        return httpInfos + httpsInfos
    }
}
