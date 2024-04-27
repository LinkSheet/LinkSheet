package fe.linksheet.extension.android

import android.content.pm.ApplicationInfo
import fe.linksheet.util.BitFlagUtil


private val systemAppFlag = BitFlagUtil.or(ApplicationInfo.FLAG_SYSTEM, ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)

fun ApplicationInfo.isUserApp(): Boolean {
    return (flags and systemAppFlag) == 0
}
