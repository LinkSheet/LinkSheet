package fe.linksheet.extension.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle


fun Intent.newIntent(action: String, uri: Uri?, dropExtras: Boolean = false): Intent {
    val intent = Intent(this)

    intent.action = action
    intent.data = uri
    intent.flags = this@newIntent.flags and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.inv()
    intent.`package` = null
    intent.component = null

    if (dropExtras) {
        intent.replaceExtras(null as? Bundle?)
    }

    return intent
}

fun Intent.newIntent(action: String, uri: Uri?, dropExtras: List<String>?): Intent {
    val intent = newIntent(action, uri, dropExtras == null)
    if (dropExtras != null) {
        for (extra in dropExtras) intent.removeExtra(extra)
    }

    return intent
}


fun shareUri(uri: Uri?): Intent {
    return shareUri(uri.toString())
}

fun shareUri(uri: String?): Intent {
    return Intent.createChooser(Intent().apply {
        this.action = Intent.ACTION_SEND
        this.type = "text/plain"
        this.putExtra(Intent.EXTRA_TEXT, uri)
    }, null)
}
