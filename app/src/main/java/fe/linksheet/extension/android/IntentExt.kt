package fe.linksheet.extension.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle


fun Intent.newIntent(action: String, uri: Uri?, dropCategories: Boolean = false, dropExtras: Boolean = false): Intent {
    val intent = Intent(this)

    intent.action = action
    intent.data = uri
    intent.flags = this@newIntent.flags and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.inv()
    intent.`package` = null
    intent.component = null

    if(dropCategories && intent.categories != null) {
        for (category in intent.categories) {
            intent.removeCategory(category)
        }
    }

    if (dropExtras) {
        intent.replaceExtras(null as? Bundle?)
    }

    return intent
}

fun shareUri(uri: Uri?): Intent {
    return Intent.createChooser(Intent().apply {
        this.action = Intent.ACTION_SEND
        this.type = "text/plain"
        this.putExtra(Intent.EXTRA_TEXT, uri?.toString())
    }, null)
}
