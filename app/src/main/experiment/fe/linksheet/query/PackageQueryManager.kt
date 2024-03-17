package fe.linksheet.query

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

        val result = context.packageManager.queryIntentActivitiesCompat(mainIntent, PackageManager.MATCH_ALL)

        return result.filter { dvm.getDomainVerificationUserState(it.activityInfo.packageName)
                ?.hostToStateMap?.containsKey(host) == true
        }
    }

    private fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: Int): MutableList<ResolveInfo> {
        return if (AndroidVersion.AT_LEAST_API_33_T) {
            queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
        } else queryIntentActivities(intent, flags)
    }
}
