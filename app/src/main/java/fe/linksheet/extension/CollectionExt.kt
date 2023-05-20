package fe.linksheet.extension

fun <K, V> Collection<K>.associateWith(collection: Collection<V>): Map<K, V>? {
    if (size != collection.size) return null

    val iterator1 = iterator()
    val iterator2 = collection.iterator()

    return buildMap {
        while (iterator1.hasNext()) {
            this[iterator1.next()] = iterator2.next()
        }
    }
}

inline fun <T> Collection<T>.forEachElementIndex(
    action: (element: T, index: Int, first: Boolean, last: Boolean) -> Unit
) {
    for ((index, element) in this.withIndex()) {
        action(element, index, index == 0, index + 1 == this.size)
    }
}
