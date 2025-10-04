package fe.linksheet.module.resolver.util

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import app.linksheet.feature.browser.Browser
import fe.linksheet.interconnect.LinkSheetConnector
import fe.linksheet.feature.app.ActivityAppInfo
import fe.linksheet.util.AndroidUri
import fe.linksheet.util.Scheme

interface IntentLauncher {
    fun launch(info: ActivityAppInfo, intent: Intent, referrer: Uri?, browser: Browser?): LaunchIntent
}

class DefaultIntentLauncher(
    val getComponentEnabledSetting: (ComponentName) -> Int,
    val showAsReferrer: () -> Boolean,
    val selfPackage: String,
) : IntentLauncher {

    override fun launch(info: ActivityAppInfo, intent: Intent, referrer: Uri?, browser: Browser?): LaunchIntent {
        if (isComponentDisabled(info)) {
            return LaunchMainIntent(createMainIntent(intent, info.packageName))
        }

        browser?.requestPrivateBrowsing(intent)

        intent.component = info.componentName
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val showAsReferrer = showAsReferrer()
        intent.putExtra(
            LinkSheetConnector.EXTRA_REFERRER,
            if (showAsReferrer) AndroidUri.create(Scheme.Package, selfPackage) else referrer
        )

        if (!showAsReferrer) {
            intent.putExtra(Intent.EXTRA_REFERRER, referrer)
        }

        return LaunchViewIntent(intent)
    }

    private fun isComponentDisabled(info: ActivityAppInfo): Boolean {
        val status = getComponentEnabledSetting(info.componentName)
        return status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    }

    private fun createMainIntent(intent: Intent, packageName: String): Intent {
        val mainIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        mainIntent.selector = intent
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setPackage(packageName)

        return mainIntent
    }
}

sealed class LaunchIntent(val intent: Intent) {

}

class LaunchRawIntent(intent: Intent) : LaunchIntent(intent)
class LaunchMainIntent(intent: Intent) : LaunchIntent(intent)
class LaunchViewIntent(intent: Intent) : LaunchIntent(intent)
