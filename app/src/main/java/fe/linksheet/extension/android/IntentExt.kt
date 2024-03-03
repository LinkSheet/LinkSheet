package fe.linksheet.extension.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import fe.linksheet.extension.koin.injectLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


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

fun Intent.getUri(): String? {
    var uriData = dataString
    if (uriData == null) {
        uriData = getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
    }

    if (uriData == null) {
        uriData = getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
    }

    return uriData
}

object IntentExt : KoinComponent {
    fun Intent.getUri(): Uri? {
        var uriData = dataString
        if (uriData == null) uriData = getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
        if (uriData == null) uriData = getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
        if (uriData != null) return runCatching { Uri.parse(uriData) }.getOrNull()
        return null
    }
}

fun shareUri(uri: Uri?): Intent {
    return Intent.createChooser(Intent().apply {
        this.action = Intent.ACTION_SEND
        this.type = "text/plain"
        this.putExtra(Intent.EXTRA_TEXT, uri?.toString())
    }, null)
}
