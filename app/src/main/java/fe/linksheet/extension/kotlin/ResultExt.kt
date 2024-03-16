package fe.linksheet.extension.kotlin


fun <T> Result.Companion.failure(msg: String? = null): Result<T> {
    val ex = if (msg != null) Exception(msg) else Exception()
    return failure(ex)
}

inline fun <T, reified R> Result<T>.unwrapOrNull(): R? where R : T {
    return getOrNull() as? R
}

//fun <T, R> Result<T>.rewrapOrNull(): Result<R>? {
//    val exception = exceptionOrNull() ?: return null
//    return Result.failure(exception)
//}
