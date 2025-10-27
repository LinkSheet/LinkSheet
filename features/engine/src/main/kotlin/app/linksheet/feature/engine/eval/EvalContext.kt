package app.linksheet.feature.engine.eval

interface EvalContext {
    fun <T> get(name: String): T
}

class EvalContextImpl(vararg inputs: Pair<String, Any?>) : EvalContext {
    private val inputs = mapOf(*inputs)

    override fun <T> get(name: String): T {
        val value = inputs[name] ?: error("Input '$name' not found")
        @Suppress("UNCHECKED_CAST")
        return value as? T ?: error("Input '$name' is not of type")
    }
}
