package fe.linksheet.util

infix fun <A, B, C> A.to(that: Pair<B, C>) = Triple(this, that.first, that.second)