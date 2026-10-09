package fe.fastforwardkt

import java.net.URLDecoder
import java.util.Base64

internal fun String.decodeBase64() = kotlin.runCatching { String(Base64.getDecoder().decode(this)) }.getOrNull()
internal fun String.decodeUrl() = kotlin.runCatching { URLDecoder.decode(this, "utf-8") }.getOrNull()
internal fun String.decodeHex(): String {
    val list = mutableListOf<Int>()
    for (i in 0 until this.length - 1 step 2) {
        list.add(this.substring(i, 2).toInt(16))
    }

    return list.map { Character.toChars(it) }.joinToString()
}
