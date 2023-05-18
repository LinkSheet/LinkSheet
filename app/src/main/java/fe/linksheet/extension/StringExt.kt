package fe.linksheet.extension

fun String.substringNullable(startIndex: Int, endIndex: Int = length) = kotlin.runCatching {
    this.substring(startIndex, endIndex)
}.getOrNull()
