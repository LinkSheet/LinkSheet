package fe.linksheet.web

import android.net.CompatUriHost
import android.net.Uri
import android.net.compatHost
import fe.std.result.isFailure
import fe.std.result.tryCatch
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

    fun getHostType(uri: Uri): HostType? {
        return getHostType(uri.compatHost)
    }

    fun getHostType(compatUriHost: CompatUriHost?): HostType? {
        if (compatUriHost == null || compatUriHost.value.isEmpty()) return null
        val hostStr = compatUriHost.value
        if (hostStr in local) return HostType.Local

        val idx = hostStr.lastIndexOf(".")
        // e.g. ".local" -> Invalid host
        if (idx == 0 && hostStr.length > 1) return null

        if (idx > 0) {
            val tld = hostStr.substring(idx)
            if (tld in mDnsTlds) return HostType.Mdns
        }

        val hostName = HostName(hostStr)
        if (!hostName.isAddress) {
            return HostType.Host
        }

        val result = tryCatch { hostName.asAddress() }
        if (result.isFailure()) {
            return null
        }

        val address = result.value
        if(address.isLocal) {
            return HostType.Local
        }

        if(address.isLoopback) {
            return HostType.Loopback
        }

        return HostType.Host
    }
}

sealed interface HostType {
    data object Local : HostType
    data object Mdns : HostType
    data object Loopback : HostType
    data object Host : HostType
}
