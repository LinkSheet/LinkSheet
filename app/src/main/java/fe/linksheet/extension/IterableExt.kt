package fe.linksheet.extension

inline fun <T> Iterable<T>.findIndexed(predicate: (T) -> Boolean) = firstOrNullIndexed(predicate)

inline fun <T> Iterable<T>.firstOrNullIndexed(predicate: (T) -> Boolean): Pair<T, Int>? {
    for ((index, element) in this.withIndex()) if (predicate(element)) return element to index
    return null
}

fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int = if (this is Collection<*>) this.size else default

inline fun <T, R> Iterable<T>.mapToSet(transform: (T) -> R) = mapTo(
    HashSet(collectionSizeOrDefault(10)), transform
)

inline fun <T> Iterable<T>.filterIf(
    condition: Boolean,
    predicate: (T) -> Boolean
) = if (condition) filter(predicate) else this

fun <T> Iterable<T>.filterNullable(
    predicate: ((T) -> Boolean)? = null
) = if(predicate != null) filter(predicate) else this