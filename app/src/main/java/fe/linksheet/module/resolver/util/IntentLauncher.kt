package fe.linksheet.module.resolver.util

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import fe.linksheet.interconnect.LinkSheetConnector
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.resolver.KnownBrowser

interface IntentLauncher {
    fun launch(info: ActivityAppInfo, intent: Intent, referrer: Uri?, browser: KnownBrowser?): LaunchIntent
}

class DefaultIntentLauncher(
    val getComponentEnabledSetting: (ComponentName) -> Int,
    val showAsReferrer: () -> Boolean,
    val selfPackage: String,
) : IntentLauncher {

    override fun launch(info: ActivityAppInfo, intent: Intent, referrer: Uri?, browser: KnownBrowser?): LaunchIntent {
        if (isComponentDisabled(info)) {
            return LaunchMainIntent(createMainIntent(intent, info.packageName))
        }

        browser?.requestPrivateBrowsing(intent)

        intent.component = info.componentName
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val showAsReferrer = showAsReferrer()
        intent.putExtra(
            LinkSheetConnector.EXTRA_REFERRER,
            if (showAsReferrer) ReferrerHelper.createReferrer(selfPackage) else referrer
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

class LaunchMainIntent(intent: Intent) : LaunchIntent(intent)
class LaunchViewIntent(intent: Intent) : LaunchIntent(intent)
