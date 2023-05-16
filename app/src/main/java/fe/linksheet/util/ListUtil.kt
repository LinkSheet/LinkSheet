package fe.linksheet.util

inline fun <T, V> keyedMap(transform: (T) -> V, elements: Array<T>) = elements.associateBy {
    transform(it)
}

fun <T, V> lazyKeyedMap(
    transform: (T) -> V,
    elements: () -> Array<T>
) = lazy { keyedMap(transform, elements()) }