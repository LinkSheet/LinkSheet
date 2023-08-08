package fe.linksheet.extension.android

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.AndroidVersion

fun Activity.startActivityWithConfirmation(intent: Intent) = kotlin.runCatching {
    this.startActivity(intent)
    true
}.onFailure { it.printStackTrace() }.getOrDefault(false)

@Deprecated(message = "Centralise this somewhere")
fun Activity.startPackageInfoActivity(info: DisplayActivityInfo): Boolean {
    return this.startActivityWithConfirmation(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        this.data = Uri.parse("package:${info.packageName}")
    })
}

fun Activity.showToast(
    @StringRes textId: Int,
    duration: Int = Toast.LENGTH_SHORT,
    uiThread: Boolean = false
) = showToast(getString(textId), duration, uiThread)

fun Activity.showToast(
    text: String,
    duration: Int = Toast.LENGTH_SHORT,
    uiThread: Boolean = false
) {
    val toast = { Toast.makeText(this, text, duration).show() }

    if (uiThread) runOnUiThread(toast)
    else toast()
}

fun Activity.initPadding() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
        v.setPadding(0, 0, 0, 0)
        insets
    }

    window.setBackgroundDrawable(ColorDrawable(0))
    window.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT
    )

    val type = if (AndroidVersion.AT_LEAST_API_26_O) {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

    window.setType(type)
}