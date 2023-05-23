package fe.linksheet.extension

fun String.substringNullable(startIndex: Int, endIndex: Int = length) = kotlin.runCatching {
    this.substring(startIndex, endIndex)
}.getOrNull()

fun String.decodeHex(): ByteArray {
    // From https://stackoverflow.com/a/67430493
    check(length % 2 == 0) { "Must have an even length" }

    val byteIterator = chunkedSequence(2)
        .map { it.toInt(16).toByte() }
        .iterator()

    return ByteArray(length / 2) { byteIterator.next() }
}
