package fe.linksheet.composable.util

fun groupSize(base: Int, vararg optional: Boolean): Int {
    return base + optional.count { it }
}
