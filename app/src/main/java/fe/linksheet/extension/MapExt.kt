package fe.linksheet.extension

inline fun <K, V> Map<K, V>.filterIf(
    condition: Boolean,
    predicate: (Map.Entry<K, V>) -> Boolean
) = if (condition) filter(predicate) else this

fun <K, V> Map<K, V>.filterIfFilterIsNotNull(
    predicate: ((Map.Entry<K, V>) -> Boolean)? = null
) = if (predicate != null) filter(predicate) else this