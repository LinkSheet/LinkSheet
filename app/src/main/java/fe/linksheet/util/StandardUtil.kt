package fe.linksheet.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <R> runIfOrNull(condition: Boolean, block: () -> R) = if (condition) block() else null
inline fun runIf(condition: Boolean, block: () -> Unit) = if (condition) block() else Unit
inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    if (condition) block()
    return this
}