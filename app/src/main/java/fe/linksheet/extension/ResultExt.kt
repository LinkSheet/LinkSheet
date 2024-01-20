package fe.linksheet.extension


fun <T> Result.Companion.failure(msg: String): Result<T> {
    return failure(Exception(msg))
}

inline fun <T, reified R> Result<T>.unwrapOrNull(): R? where R : T {
    return getOrNull() as? R
}
