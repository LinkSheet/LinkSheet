package fe.linksheet.extension

fun String.substringNullable(startIndex: Int, endIndex: Int = length): String? {
    return kotlin.runCatching { this.substring(startIndex, endIndex) }.getOrNull()
}