package fe.buildsrc

import groovy.lang.Closure

class KotlinClosure4<in T : Any?, in U : Any?, in V : Any?, in W : Any?, R : Any>(
    val function: (T, U, V, W) -> R?,
    owner: Any? = null,
    thisObject: Any? = null
) : Closure<R?>(owner, thisObject) {
    @Suppress("unused")
    fun doCall(t: T, u: U, v: V, w: W): R? = function(t, u, v, w)
}
