package fe.linksheet.extension

inline fun <T> Iterable<T>.findIndexed(predicate: (T) -> Boolean): Pair<T, Int>? {
    return firstOrNullIndexed(predicate)
}

inline fun <T> Iterable<T>.firstOrNullIndexed(predicate: (T) -> Boolean): Pair<T, Int>? {
    for ((index, element) in this.withIndex()) if (predicate(element)) return element to index
    return null
}
