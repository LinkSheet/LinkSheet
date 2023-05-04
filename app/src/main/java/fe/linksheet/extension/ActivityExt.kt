package fe.linksheet.extension

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes

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
