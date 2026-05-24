package fe.linksheet.testlib.instrument.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class AppInteractor(private val context: Context) {
    private val targetPackageName = context.packageName

    fun getTestAppInfo(): TestApp {
        val appInfo = context.packageManager.getApplicationInfo(
            targetPackageName,
            PackageManager.MATCH_ALL
        )
        val label = context.packageManager.getApplicationLabel(appInfo)
        return TestApp(appInfo, label.toString(), targetPackageName)
    }
}

data class TestApp(
    val appInfo: ApplicationInfo,
    val label: String,
    val packageName: String
)
