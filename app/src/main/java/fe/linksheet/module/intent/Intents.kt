package fe.linksheet.module.intent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import fe.linksheet.BuildConfig

object Intents {
    fun createSelfIntent(uri: Uri?, extras: Bundle? = null): Intent {
        return buildIntent(Intent.ACTION_VIEW, uri) {
            `package` = BuildConfig.APPLICATION_ID
            extras?.let { putExtras(it) }
        }
    }

    fun cloneIntent(intent: Intent, action: String, uri: Uri?, dropExtras: Boolean = false): Intent {
        val intent = Intent(intent)

        intent.action = action
        intent.data = uri
        intent.flags = intent.flags and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.inv()
        intent.`package` = null
        intent.component = null

        if (dropExtras) {
            intent.replaceExtras(null as? Bundle?)
        }

        return intent
    }

    fun createShareUriIntent(uri: Uri?): Intent {
        return createShareUriIntent(uri.toString())
    }

    fun createShareUriIntent(uri: String?): Intent {
        return buildIntent(Intent.ACTION_SEND) {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, uri)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

fun Intent.cloneIntent(action: String, uri: Uri?, dropExtras: Boolean = false): Intent {
    return Intents.cloneIntent(this, action, uri, dropExtras)
}
