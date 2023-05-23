package fe.linksheet.extension

fun ByteArray.toHex() = joinToString(separator = "") { eachByte ->
    "%02x".format(eachByte)
}