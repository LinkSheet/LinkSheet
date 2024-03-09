package fe.linksheet.util

import android.net.Uri
import inet.ipaddr.HostName




object HostUtil {
    private val mDnsTlds = setOf(".local", ".test", ".example", ".invalid", ".localhost")
    private val localhost = setOf("localhost", "127.0.0.1", "0.0.0.0")

    private const val httpsScheme = "https://"

    // TODO: Does not look very robust
    fun cleanHttpsScheme(host: String): String {
        val hostWithoutScheme = if (host.indexOf(httpsScheme) != -1) {
            host.substring(httpsScheme.length)
        } else host

        return if (hostWithoutScheme.endsWith("/")) hostWithoutScheme.substring(
            0,
            hostWithoutScheme.length - 2
        ) else hostWithoutScheme
    }

    fun isAccessiblePublicly(uri: Uri): Boolean? {
        return isAccessiblePublicly(uri.host)
    }

    fun isAccessiblePublicly(hostStr: String?): Boolean? {
        if (hostStr.isNullOrEmpty()) return false
        if (hostStr in localhost) return false

        val idx = hostStr.lastIndexOf(".")
        if (idx == 0) return false

        if (idx > 0) {
            val tld = hostStr.substring(idx)
            if (tld in mDnsTlds) return false
        }

        val host = HostName(hostStr)
        val address = try {
            host.asAddress()
        } catch (e: Exception) {
            return null
        }

        return !(address.isLocal || address.isLoopback)
    }
}
