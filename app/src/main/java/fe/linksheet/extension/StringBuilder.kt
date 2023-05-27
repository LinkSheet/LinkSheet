package fe.linksheet.extension

import javax.crypto.Mac

fun StringBuilder.appendHashed(mac: Mac, string: String) = append(mac.hash(string))

