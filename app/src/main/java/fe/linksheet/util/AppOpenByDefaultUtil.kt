package fe.linksheet.util

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
fun getAppOpenByDefaultIntent(packageName: String): Intent {
    return if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
        // S*msung moment lol (https://stackoverflow.com/a/72365164)
        Intent("android.settings.MANAGE_DOMAIN_URLS")
    } else {
        Intent(
            Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
            Uri.parse("package:${packageName}")
        )
    }
}