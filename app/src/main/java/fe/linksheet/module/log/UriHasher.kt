package fe.linksheet.module.log

import fe.linksheet.extension.appendHashed
import fe.linksheet.extension.appendHashedTrim
import fe.uribuilder.ParsedUri
import fe.uribuilder.UriParser
import org.apache.hc.core5.net.InetAddressUtils
import org.apache.hc.core5.net.PercentCodec
import org.apache.hc.core5.util.TextUtils
import javax.crypto.Mac

fun buildHashedUriString(uriString: String, mac: Mac): String {
    var uri = UriParser.parseUri(uriString)

    return buildString {
        if (uri.scheme != null) {
            append(uri.scheme).append(":")
        }

        if (uri.encodedSchemeSpecificPart != null) {
            uri = UriParser.parseUri(uri.encodedSchemeSpecificPart!!)
        }

        if (uri.encodedFragment != null) {
            append("#").appendHashedTrim(mac, 6, uri.encodedFragment)
        } else if (uri.fragment != null) {
            append("#")
            appendHashedTrim(mac, 6, buildString {
                PercentCodec.encode(this, uri.fragment, uri.charset)
            })
        }

        this.parse(uri, mac)
    }
}

private fun StringBuilder.parse(uri: ParsedUri, mac: Mac) {
    val authoritySpecified: Boolean = if (uri.encodedAuthority != null) {
        append("//").appendHashedTrim(mac, 6, uri.encodedAuthority)
        true
    } else if (uri.host != null) {
        append("//")
        if (uri.encodedUserInfo != null) {
            appendHashed(mac, uri.encodedUserInfo).append("@")
        } else if (uri.userInfo != null) {
            val idx = uri.userInfo!!.indexOf(':')
            if (idx != -1) {
                appendHashedTrim(mac, 6, buildString {
                    PercentCodec.encode(
                        this,
                        uri.userInfo!!.substring(0, idx),
                        uri.charset
                    )
                })
                append(':')
                appendHashedTrim(mac, 6, buildString {
                    PercentCodec.encode(
                        this,
                        uri.userInfo!!.substring(idx + 1),
                        uri.charset
                    )
                })
            } else {
                appendHashedTrim(mac, 6,
                    buildString {
                        PercentCodec.encode(
                            this,
                            uri.userInfo,
                            uri.charset
                        )
                    })
            }
            append("@")
        }

        if (InetAddressUtils.isIPv6Address(uri.host)) {
            append("[").appendHashedTrim(mac, 6, uri.host).append("]")
        } else {
            appendHashed(mac, PercentCodec.encode(uri.host, uri.charset))
        }
        if (uri.port >= 0) {
            append(":").appendHashedTrim(mac, 6, uri.port.toString())
        }

        true
    } else false

    if (uri.encodedPath != null) {
        if (authoritySpecified && !TextUtils.isEmpty(uri.encodedPath) && !uri.encodedPath!!.startsWith(
                "/"
            )
        ) {
            append("/")
        }

        uri.encodedPath!!.split("/").forEach {
            appendHashedTrim(mac, 6, it).append("/")
        }
    } else if (uri.pathSegments.isNotEmpty()) {
        uri.pathSegments.forEach {
            appendHashedTrim(mac, 6, it).append("/")
        }
    }

    if (uri.encodedQuery != null) {
        append("?").appendHashed(mac, uri.encodedQuery)
    } else if (uri.queryParams.isNotEmpty()) {
        append("?")

        val query = buildString {
            uri.formatQuery(this, uri.queryParams, uri.charset, false)
        }

        query.split("&").forEach {
            appendHashed(mac, it).append("&")
        }
    }
}