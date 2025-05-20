package fe.linksheet.module.resolver.util

import android.content.Intent
import android.net.Uri
import fe.linksheet.util.IntentFlags
import mozilla.components.support.utils.SafeIntent

open class IntentSanitizer internal constructor(
    private val removeFlags: IntentFlags
) {
    companion object Default : IntentSanitizer(
        removeFlags = IntentFlags.select(
            IntentFlags.ACTIVITY_FORWARD_RESULT,
            IntentFlags.ACTIVITY_EXCLUDE_FROM_RECENTS
        )
    )

    fun sanitize(intent: SafeIntent, action: String, uri: Uri?, dropExtras: List<String>?): Intent {
        val flags = with(removeFlags) { intent.flags.remove() }
        val sanitized = newIntent(action, uri, flags)

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
