package fe.linksheet.extension

inline fun <K, V> MutableMap<K, V>.putOrUpdateIf(
    key: K,
    value: () -> V,
    predicate: (existing: V, value: V) -> Boolean
): V {
    val newValue = value()
    val existingValue = this[key]

    return if (existingValue == null || predicate(existingValue, newValue)) {
        put(key, newValue)
        newValue
    } else {
        existingValue
    }
}