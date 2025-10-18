package fe.embed.resolve

import assertk.assertAll

inline fun <T> assertEach(iterable: Iterable<T>, f: (T) -> Unit) {
    assertAll {
        for (element in iterable) {
            f(element)
        }
    }
}
