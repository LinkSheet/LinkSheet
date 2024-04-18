package fe.linksheet.experiment.new.query.manager.query

import android.content.Context
import android.content.Intent
import android.content.IntentFilter.AuthorityEntry
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.queryIntentActivitiesCompat
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import fe.linksheet.module.resolver.UriViewActivity
import fe.linksheet.util.BitFlagUtil
import org.koin.core.component.KoinComponent


object PackageQueryManager : KoinComponent {
    private val QUERY_FLAGS = BitFlagUtil.or(
        PackageManager.MATCH_ALL,
        PackageManager.GET_RESOLVED_FILTER,
        PackageManager.MATCH_DISABLED_COMPONENTS
    )

    fun findHandlers(context: Context, uri: Uri): List<UriViewActivity> {
        val viewIntent = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
        val activities = context.packageManager.queryIntentActivitiesCompat(viewIntent, QUERY_FLAGS)

        return activities.filter { it.isLinkHandler(uri) }.map { UriViewActivity(it, false) }
    }

    private val anyHost = AuthorityEntry("*", "-1")

    private fun ResolveInfo.isLinkHandler(uri: Uri): Boolean {
        val count = filter.countDataAuthorities()
        if (count == 0) return false
        if (count == 1 && filter.getDataAuthority(0) == anyHost) return false

        return filter.hasDataAuthority(uri)
    }
}
