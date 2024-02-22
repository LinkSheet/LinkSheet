package fe.linksheet.module.preference.permission

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.content.getSystemService
import fe.linksheet.R
import fe.linksheet.extension.android.startActivityWithConfirmation

class UsageStatsPermission(private val context: Context) : PermissionBoundPreference(
    R.string.usage_stats_sorting, R.string.usage_stats_sorting_explainer
) {

    private val appOpsManager = context.getSystemService<AppOpsManager>()!!

    override fun check(): Boolean {
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )

        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun request(context: Activity): Boolean {
        return context.startActivityWithConfirmation(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }
}

