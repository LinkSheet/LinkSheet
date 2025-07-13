package fe.linksheet.util.intent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import fe.composekit.intent.buildIntent
import fe.linksheet.BuildConfig
import fe.linksheet.util.AndroidUriHelper
import fe.linksheet.util.create

object StandardIntents {
    fun createAppSettingsIntent(packageName: String): Intent {
        return buildIntent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            AndroidUriHelper.Type.Package.create(packageName)
        ) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun createSelfIntent(uri: Uri?, extras: Bundle? = null): Intent {
        return buildIntent(Intent.ACTION_VIEW, uri) {
            `package` = BuildConfig.APPLICATION_ID
            extras?.let { putExtras(it) }
        }
    }
}
