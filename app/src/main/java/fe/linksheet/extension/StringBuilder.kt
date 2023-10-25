package fe.linksheet.extension

import javax.crypto.Mac

fun StringBuilder.appendHashed(mac: Mac, string: String?): StringBuilder = append(string?.let { mac.hash(it) })

fun StringBuilder.appendHashedTrim(
    mac: Mac,
    length: Int = 6,
    string: String?
): StringBuilder = append(string?.let { mac.hash(it).substring(0, length) })
