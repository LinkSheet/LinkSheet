package app.linksheet.feature.engine.engine.resolver

import android.net.Uri
import fe.linksheet.extension.toAndroidUri
import fe.linksheet.web.Darknet
import fe.linksheet.web.HostType
import fe.linksheet.web.HostUtil
import fe.std.uri.StdUrl

class UrlChecker(
    private val allowDarknets: () -> Boolean,
    private val allowNonPublic: () -> Boolean
) {
    internal fun isPublic(uri: Uri): Boolean {
        return HostUtil.getHostType(uri) == HostType.Host
    }

    internal fun isDarknet(uri: Uri): Boolean {
        return Darknet.getOrNull(uri) != null
    }

    fun check(url: StdUrl): ResolveOutput? {
        val uri = url.toAndroidUri()
        val isDarknet = isDarknet(uri)
        if (!allowDarknets() && isDarknet) {
            return ResolveOutput(url)
        }

        val isPublic = isPublic(uri)
        if (!allowNonPublic() && !isPublic) {
            return ResolveOutput(url)
        }

        return null
    }
}
