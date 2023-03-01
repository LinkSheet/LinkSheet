package fe.linksheet.extension

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.tasomaniac.openwith.resolver.DisplayActivityInfo

fun Context.startActivityWithConfirmation(intent: Intent): Boolean {
    return try {
        this.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    }
}

fun Context.startPackageInfoActivity(info: DisplayActivityInfo): Boolean {
    return this.startActivityWithConfirmation(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        this.data = Uri.parse("package:${info.packageName}")
    })
}