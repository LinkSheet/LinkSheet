package fe.linksheet.extension.java

import javax.crypto.Mac

@OptIn(ExperimentalStdlibApi::class)
fun Mac.hash(string: String) = this.doFinal(string.toByteArray()).toHexString()
