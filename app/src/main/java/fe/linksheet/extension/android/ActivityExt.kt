package fe.linksheet.extension.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Deprecated(
    "Use tryStartActivity",
    replaceWith = ReplaceWith("this.tryStartActivity(intent)", "fe.linksheet.util.extension.android.tryStartActivity")
)
fun Activity.startActivityWithConfirmation(intent: Intent) = kotlin.runCatching {
    this.startActivity(intent)
    true
}.onFailure { it.printStackTrace() }.getOrDefault(false)

suspend fun Context.showToast(
    @StringRes textId: Int,
    duration: Int = Toast.LENGTH_SHORT
) = withContext(Dispatchers.Main) {
    Toast.makeText(this@showToast, getString(textId), duration).show()
}

fun Activity.showToast(
    @StringRes textId: Int,
    duration: Int = Toast.LENGTH_SHORT,
    uiThread: Boolean = false,
) = showToast(getString(textId), duration, uiThread)

fun Activity.showToast(
    text: String,
    duration: Int = Toast.LENGTH_SHORT,
    uiThread: Boolean = false,
) {
    val toast = { Toast.makeText(this, text, duration).show() }

    if (uiThread) runOnUiThread(toast)
    else toast()
}
