package fe.linksheet.extension.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import fe.linksheet.module.log.LoggerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


fun Intent.newIntent(uri: Uri?, dropExtras: Boolean = false) = Intent(this).apply {
    action = Intent.ACTION_VIEW
    data = uri
    flags = this@newIntent.flags and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.inv()
    `package` = null
    component = null

    if (dropExtras) {
        replaceExtras(null as? Bundle?)
    }
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
    private val loggerFactory by inject<LoggerFactory>()
    private val logger = loggerFactory.createLogger(IntentExt::class)


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
