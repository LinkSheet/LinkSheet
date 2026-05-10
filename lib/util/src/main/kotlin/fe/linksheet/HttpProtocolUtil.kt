package fe.linksheet

fun String.maybePrependProtocol(proto: String): String {
    val fullProto = when {
        !proto.endsWith("://") -> "$proto://"
        else -> proto
    }

    return when {
        !this.startsWith(fullProto) -> fullProto + this
        else -> this
    }
}
