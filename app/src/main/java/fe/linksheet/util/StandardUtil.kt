package fe.linksheet.util

inline fun <R> runIf(condition: Boolean, block: () -> R) = if (condition) block() else null
inline fun runIf(condition: Boolean, block: () -> Unit) = if (condition) block() else Unit