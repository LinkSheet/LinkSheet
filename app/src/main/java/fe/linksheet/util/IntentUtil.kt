package fe.linksheet.util

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import fe.linksheet.BuildConfig

fun selfIntent(uri: Uri?, extras: Bundle? = null) = Intent().apply {
    action = Intent.ACTION_VIEW
    data = uri
    `package` = BuildConfig.APPLICATION_ID
    extras?.let { putExtras(extras) }
}