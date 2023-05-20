package fe.linksheet.extension

inline fun <T> Array<out T>.forEachElementIndex(
    action: (element: T, index: Int, first: Boolean, last: Boolean) -> Unit
) {
    for ((index, element) in this.withIndex()) {
        action(element, index, index == 0, index + 1 == this.size)
    }
}
