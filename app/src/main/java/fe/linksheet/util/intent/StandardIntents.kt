package fe.linksheet.util.intent

import android.content.Intent
import android.provider.Settings
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
}
