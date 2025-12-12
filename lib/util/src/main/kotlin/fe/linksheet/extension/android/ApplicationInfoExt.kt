package fe.linksheet.extension.android

import android.content.pm.ApplicationInfo
import fe.linksheet.util.ApplicationInfoPrivateFlags


val SYSTEM_APP_FLAGS = ApplicationInfoPrivateFlags.select(
    ApplicationInfoPrivateFlags.SYSTEM,
    ApplicationInfoPrivateFlags.UPDATED_SYSTEM_APP
)

val ApplicationInfo.isSystemApp: Boolean
    get() = flags in SYSTEM_APP_FLAGS

val ApplicationInfo.isUserApp: Boolean
    get() = flags !in SYSTEM_APP_FLAGS

