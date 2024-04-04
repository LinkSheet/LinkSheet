package fe.linksheet.experiment.new.query.manager.query

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.linksheet.util.AndroidVersion

object PackageQueryManager {

    @RequiresApi(Build.VERSION_CODES.S)
    fun findHandlers(context: Context, uri: Uri): List<ResolveInfo> {
        val dvm = context.getSystemService<DomainVerificationManager>()!!
        val host = uri.host.toString()

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val viewIntent = Intent(Intent.ACTION_VIEW, uri)

        val packageManager = context.packageManager
        val result = context.packageManager.queryIntentActivitiesCompat(mainIntent, PackageManager.MATCH_ALL)
        return result.asSequence()
            .filter { it.canHandle(dvm, host) }
            .filter { it.hasActivity(packageManager, viewIntent) != null }
            .toList()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun ResolveInfo.canHandle(dvm: DomainVerificationManager, host: String): Boolean {
        // TODO: Does this work for wildcard subdomains? (*.example.org?)
        return dvm.getDomainVerificationUserState(activityInfo.packageName)?.hostToStateMap?.containsKey(host) == true
    }

    private fun ResolveInfo.hasActivity(packageManager: PackageManager, viewIntent: Intent): ResolveInfo? {
        return packageManager.resolveActivity(
            Intent(viewIntent).setPackage(activityInfo.packageName),
            PackageManager.MATCH_DEFAULT_ONLY
        )
    }

    private fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: Int): MutableList<ResolveInfo> {
        return if (AndroidVersion.AT_LEAST_API_33_T) {
            queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
        } else queryIntentActivities(intent, flags)
    }
}
