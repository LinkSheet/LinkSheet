package fe.linksheet.util.intent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import fe.composekit.core.Scheme
import fe.composekit.core.create
import fe.composekit.intent.buildIntent
import fe.linksheet.BuildConfig

object StandardIntents {
    fun createAppSettingsIntent(packageName: String): Intent {
        return buildIntent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Scheme.Package.create(packageName)
        ) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun createSelfIntent(uri: Uri?, extras: Bundle? = null, selfPackage: String = BuildConfig.APPLICATION_ID): Intent {
        return buildIntent(Intent.ACTION_VIEW, uri) {
            `package` = selfPackage
            extras?.let { putExtras(it) }
        }
    }

    fun createSelfIntent(intent: Intent, selfPackage: String = BuildConfig.APPLICATION_ID): Intent {
        val newIntent = Intent(intent)
        newIntent.action = Intent.ACTION_VIEW
        newIntent.`package` = selfPackage
        return newIntent
    }
}
