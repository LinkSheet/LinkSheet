package fe.linksheet.module.resolver

import android.content.pm.ResolveInfo

data class UriViewActivity(val resolveInfo: ResolveInfo, val fallback: Boolean)
