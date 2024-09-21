package fe.linksheet.experiment.improved.resolver

import android.content.Intent
import android.net.Uri
import mozilla.components.support.utils.SafeIntent

object IntentHandler {
    private const val INV_FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.inv()

    fun sanitize(intent: SafeIntent, action: String, uri: Uri?, dropExtras: List<String>?): Intent {
        val sanitized = newIntent(action, uri, intent.flags and INV_FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

        if (dropExtras != null && intent.extras != null) {
            sanitized.putExtras(intent.extras!!)
            for (extra in dropExtras) sanitized.removeExtra(extra)
        }

        return sanitized
    }

    private fun newIntent(action: String, uri: Uri?, flags: Int): Intent {
        return Intent(action, uri).setFlags(flags)
    }
}
