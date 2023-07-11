package fe.linksheet.extension

import fe.kotlin.extension.toHexString
import javax.crypto.Mac

fun Mac.hash(string: String) = this.doFinal(string.toByteArray()).toHexString()