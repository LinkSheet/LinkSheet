package fe.fastforwardkt

internal fun String.substringNullable(startIndex: Int, endIndex: Int): String? {
    return runCatching { this.substring(startIndex, endIndex) }.getOrNull()
}

internal fun String.substringNullable(startIndex: Int): String? {
    return runCatching { this.substring(startIndex) }.getOrNull()
}
