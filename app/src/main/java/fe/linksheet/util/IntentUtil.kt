package fe.linksheet.util

import android.app.Activity
import android.content.Intent

fun Intent.sourceIntent() = Intent(this).apply {
    component = null
    flags = flags and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.inv()
}