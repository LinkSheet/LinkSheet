package fe.linksheet.extension

import fe.linksheet.util.to


inline fun <T> Iterable<T>.findIndexed(predicate: (T) -> Boolean) = firstOrNullIndexed(predicate)

inline fun <T> Iterable<T>.firstOrNullIndexed(predicate: (T) -> Boolean): Pair<T, Int>? {
    for ((index, element) in this.withIndex()) if (predicate(element)) return element to index
    return null
}

fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int =
    if (this is Collection<*>) this.size else default

inline fun <T, R> Iterable<T>.mapToSet(transform: (T) -> R) = mapTo(
    HashSet(collectionSizeOrDefault(10)), transform
)

inline fun <T> Iterable<T>.filterIf(
    condition: Boolean,
    predicate: (T) -> Boolean
) = if (condition) filter(predicate) else this

fun <T> Iterable<T>.filterIfFilterIsNotNull(
    predicate: ((T) -> Boolean)? = null
) = if (predicate != null) filter(predicate) else this

fun mapCapacity(expectedSize: Int): Int = when {
    // We are not coercing the value to a valid one and not throwing an exception. It is up to the caller to
    // properly handle negative values.
    expectedSize < 0 -> expectedSize
    expectedSize < 3 -> expectedSize + 1
    expectedSize < INT_MAX_POWER_OF_TWO -> ((expectedSize / 0.75F) + 1.0F).toInt()
    // any large value
    else -> Int.MAX_VALUE
}

private const val INT_MAX_POWER_OF_TWO: Int = 1 shl (Int.SIZE_BITS - 2)

inline fun <T, K, V> Iterable<T>.groupBy(
    keySelector: (T) -> K?,
    valueTransform: (T) -> V,
    defaultValue: () -> MutableCollection<V> = { HashSet() }
): MutableMap<K, MutableCollection<V>> {
    val destination = mutableMapOf<K, MutableCollection<V>>()
    for (element in this) {
        val key = keySelector(element)
        if (key != null) {
            val value = valueTransform(element)
            destination.getOrPut(key, defaultValue).add(value)
        }
    }
    return destination
}