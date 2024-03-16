package fe.linksheet.extension.kotlin

fun <T> MutableList<T>.setup(elements: Iterable<T>) {
    this.clear()
    this.addAll(elements)
}
