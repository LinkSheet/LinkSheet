package fe.linksheet.extension

fun <T> MutableList<T>.setup(elements: Iterable<T>) {
    this.clear()
    this.addAll(elements)
}
