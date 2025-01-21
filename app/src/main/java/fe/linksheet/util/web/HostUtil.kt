package fe.linksheet.util.web

import android.net.CompatUriHost
import android.net.Uri
import android.net.compatHost
import inet.ipaddr.HostName

object HostUtil {
    private val mDnsTlds = setOf(".local", ".test", ".example", ".invalid", ".localhost")
    private val local = setOf(
        "127.0.0.1",
        "localhost",
        "[::1]",
        "::1",
        "0000:0000:0000:0000:0000:0000:0000:0001",
        "0.0.0.0",
        "[::]",
        "[::]",
        "0000:0000:0000:0000:0000:0000:0000:0000"
    )

    private const val HTTPS_SCHEME = "https://"

    // TODO: Does not look very robust
    fun cleanHttpsScheme(host: String): String {
        val hostWithoutScheme = if (host.indexOf(HTTPS_SCHEME) != -1) {
            host.substring(HTTPS_SCHEME.length)
        } else host

        return if (hostWithoutScheme.endsWith("/")) hostWithoutScheme.substring(
            0,
            hostWithoutScheme.length - 1
        ) else hostWithoutScheme
    }

    fun isAccessiblePublicly(uri: Uri): Boolean {
        return isAccessiblePublicly(uri.compatHost)
    }

    fun isAccessiblePublicly(compatUriHost: CompatUriHost?): Boolean {
        if (compatUriHost == null || compatUriHost.value.isEmpty()) return false
        if (compatUriHost.value in local) return false

        val idx = compatUriHost.value.lastIndexOf(".")
        if (idx == 0) return false

        if (idx > 0) {
            val tld = compatUriHost.value.substring(idx)
            if (tld in mDnsTlds) return false
        }

        val host = HostName(compatUriHost.value)
        val address = try {
            host.asAddress()
        } catch (e: Exception) {
            null
        }

        if(address == null){
            return false
        }

        return !(address.isLocal || address.isLoopback)
    }
}
