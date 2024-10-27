package fe.linksheet.extension.android

import android.content.pm.ApplicationInfo
import fe.linksheet.util.ApplicationInfoPrivateFlags


private val SYSTEM_APP_FLAGS = ApplicationInfoPrivateFlags.select(
    ApplicationInfoPrivateFlags.SYSTEM,
    ApplicationInfoPrivateFlags.UPDATED_SYSTEM_APP
)

fun ApplicationInfo.isUserApp(): Boolean {
    return flags !in SYSTEM_APP_FLAGS
}
