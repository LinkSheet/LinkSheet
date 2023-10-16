package fe.linksheet.module.log

import fe.linksheet.extension.appendHashed
import fe.uribuilder.ParsedUri
import fe.uribuilder.UriParser
import org.apache.hc.core5.net.InetAddressUtils
import org.apache.hc.core5.net.PercentCodec
import org.apache.hc.core5.util.TextUtils
import javax.crypto.Mac

fun buildHashedUriString(uriString: String, mac: Mac): String {
    val uri = UriParser.parseUri(uriString)

    return buildString {
        if (uri.scheme != null) {
            append(uri.scheme).append(':')
        }

        if (uri.encodedSchemeSpecificPart != null) {
            appendHashed(mac, uri.encodedSchemeSpecificPart)
        } else {
            val authoritySpecified: Boolean = if (uri.encodedAuthority != null) {
                append("//").appendHashed(mac, uri.encodedAuthority)
                true
            } else if (uri.host != null) {
                append("//")
                if (uri.encodedUserInfo != null) {
                    appendHashed(mac, uri.encodedUserInfo).append("@")
                } else if (uri.userInfo != null) {
                    val idx = uri.userInfo!!.indexOf(':')
                    if (idx != -1) {
                        appendHashed(mac, buildString {
                            PercentCodec.encode(
                                this,
                                uri.userInfo!!.substring(0, idx),
                                uri.charset
                            )
                        })
                        append(':')
                        appendHashed(mac, buildString {
                            PercentCodec.encode(
                                this,
                                uri.userInfo!!.substring(idx + 1),
                                uri.charset
                            )
                        })
                    } else {
                        appendHashed(
                            mac,
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
                    append("[").appendHashed(mac, uri.host).append("]")
                } else {
                    appendHashed(mac, PercentCodec.encode(uri.host, uri.charset))
                }
                if (uri.port >= 0) {
                    append(":").appendHashed(mac, uri.port.toString())
                }

                true
            } else false

            if (uri.encodedPath != null) {
                if (authoritySpecified && !TextUtils.isEmpty(uri.encodedPath) && !uri.encodedPath!!.startsWith(
                        "/"
                    )
                ) {
                    append('/')
                }

                appendHashed(mac, uri.encodedPath)
            } else if (uri.pathSegments.isNotEmpty()) {
                appendHashed(mac, buildString {
                    uri.formatPath(
                        this,
                        uri.pathSegments,
                        !authoritySpecified && uri.pathRootless,
                        uri.charset
                    )
                })
            }

            if (uri.encodedQuery != null) {
                append("?").appendHashed(mac, uri.encodedQuery)
            } else if (uri.queryParams.isNotEmpty()) {
                append("?")
                appendHashed(mac, buildString {
                    uri.formatQuery(this, uri.queryParams, uri.charset, false)
                })
            }
        }

        if (uri.encodedFragment != null) {
            append("#").appendHashed(mac, uri.encodedFragment)
        } else if (uri.fragment != null) {
            append("#")
            appendHashed(mac, buildString {
                PercentCodec.encode(this, uri.fragment, uri.charset)
            })
        }
    }
}