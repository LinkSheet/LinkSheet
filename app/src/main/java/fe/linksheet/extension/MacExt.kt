package fe.linksheet.extension

import javax.crypto.Mac

fun Mac.hash(string: String) = this.doFinal(string.toByteArray()).toHex()