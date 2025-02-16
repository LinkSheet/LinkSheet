package fe.linksheet.module.redactor

import fe.hc.core5.TextUtils
import fe.kotlin.extension.asString
import fe.kotlin.extension.iterator.withElementInfo
import fe.linksheet.extension.kotlin.appendHashed
import fe.linksheet.extension.kotlin.appendHashedTrim
import fe.relocated.org.apache.hc.core5.core5.net.InetAddressUtils
import fe.relocated.org.apache.hc.core5.core5.net.PercentCodec
import fe.std.uri.ParserFailure
import fe.std.uri.Url
import javax.crypto.Mac

fun buildHashedUriString(uriString: String, mac: Mac): String {
    var url = Url(uriString)

    if (url is ParserFailure) {
        return "Something went very wrong. Parsing this url failed: ${url.exception.asString()}"
    }

    val uri = url as Url
    return buildString {
        if (uri.scheme != null) {
            append(uri.scheme).append(":")
        }

        if (uri.encodedSchemeSpecificPart != null) {
            url = Url(uri.encodedSchemeSpecificPart!!)
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

private fun StringBuilder.parse(url: Url, mac: Mac) {
    val authoritySpecified: Boolean = if (url.encodedAuthority != null) {
        append("//")
        for ((element, _, _, last) in url.encodedAuthority!!.split(".").withElementInfo()) {
            appendHashedTrim(mac, 6, element)
            if (!last) append(".")
        }
        true
    } else if (url.host != null) {
        append("//")
        if (url.encodedUserInfo != null) {
            appendHashed(mac, url.encodedUserInfo).append("@")
        } else if (url.userInfo != null) {
            val idx = url.userInfo!!.indexOf(':')
            if (idx != -1) {
                appendHashedTrim(mac, 6, buildString {
                    PercentCodec.encode(
                        this,
                        url.userInfo!!.substring(0, idx),
                        url.charset
                    )
                })
                append(':')
                appendHashedTrim(mac, 6, buildString {
                    PercentCodec.encode(
                        this,
                        url.userInfo!!.substring(idx + 1),
                        url.charset
                    )
                })
            } else {
                appendHashedTrim(mac, 6,
                    buildString {
                        PercentCodec.encode(
                            this,
                            url.userInfo,
                            url.charset
                        )
                    })
            }
            append("@")
        }

        if (InetAddressUtils.isIPv6(url.host)) {
            append("[").appendHashedTrim(mac, 6, url.host).append("]")
        } else {
            appendHashed(mac, PercentCodec.encode(url.host, url.charset))
        }
        if (url.port >= 0) {
            append(":").appendHashedTrim(mac, 6, url.port.toString())
        }

        true
    } else false

    if (url.encodedPath != null) {
        if (authoritySpecified && !TextUtils.isEmpty(url.encodedPath!!) && !url.encodedPath!!.startsWith(
                "/"
            )
        ) {
            append("/")
        }

        url.encodedPath!!.split("/").forEach {
            appendHashedTrim(mac, 6, it).append("/")
        }
    } else if (url.pathSegments.isNotEmpty()) {
        url.pathSegments.forEach {
            appendHashedTrim(mac, 6, it).append("/")
        }
    }

    if (url.encodedQuery != null) {
        append("?").appendHashed(mac, url.encodedQuery)
    } else if (url.queryParams.isNotEmpty()) {
        append("?")

        val query = url.formattedQuery

        query.split("&").forEach {
            appendHashed(mac, it).append("&")
        }
    }
}
