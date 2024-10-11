package fe.linksheet.module.redactor

import fe.kotlin.extension.asString
import fe.kotlin.extension.iterator.forEachWithInfo
import fe.linksheet.extension.kotlin.appendHashed
import fe.linksheet.extension.kotlin.appendHashedTrim
import fe.uribuilder.UriParseResult
import fe.uribuilder.UriParser
import org.apache.hc.core5.net.InetAddressUtils
import org.apache.hc.core5.net.PercentCodec
import org.apache.hc.core5.util.TextUtils
import javax.crypto.Mac

fun buildHashedUriString(uriString: String, mac: Mac): String {
    var result = UriParser.parseUri(uriString)
    if (result is UriParseResult.ParserFailure) {
        return "Something went very wrong. Parsing this url failed: ${result.exception.asString()}"
    }

    val uri = result as UriParseResult.ParsedUri
    return buildString {
        if (uri.scheme != null) {
            append(uri.scheme).append(":")
        }

        if (uri.encodedSchemeSpecificPart != null) {
            result = UriParser.parseUri(uri.encodedSchemeSpecificPart!!)
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

private fun StringBuilder.parse(uri: UriParseResult.ParsedUri, mac: Mac) {
    val authoritySpecified: Boolean = if (uri.encodedAuthority != null) {
        append("//")
        uri.encodedAuthority!!.split(".").forEachWithInfo { element, _, _, last ->
            appendHashedTrim(mac, 6, element)
            if (!last) append(".")
        }
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
            uri.formatQuery(this, uri.queryParams, uri.charset)
        }

        query.split("&").forEach {
            appendHashed(mac, it).append("&")
        }
    }
}
