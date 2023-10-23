package fe.linksheet.util


class LazyItemKeyCreator {
    private var i = 0

    fun next(): Int {
        return ++i
    }
}

fun lazyItemKeyCreator(): LazyItemKeyCreator {
    return LazyItemKeyCreator()
}