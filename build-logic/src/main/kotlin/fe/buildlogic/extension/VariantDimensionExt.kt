package fe.buildlogic.extension

import com.android.build.api.dsl.VariantDimension
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun VariantDimension.buildStringConfigField(name: String, value: String? = null) {
    buildConfigField("String", name, encodeString(value))
}

fun encodeString(value: String? = null): String {
    return if (value == null) "null" else "\"${value}\""
}

@OptIn(ExperimentalContracts::class)
fun VariantDimension.buildConfig(block: BuildConfigBlock.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val impl = buildConfigBlock()
    block(impl)
}


@DslMarker
annotation class BuildConfigDslMarker

@BuildConfigDslMarker
interface BuildConfigBlock {
    fun string(name: String, value: String?)
    fun stringArray(name: String, values: Iterable<String?>)
    fun int(name: String, value: Int)
    fun long(name: String, value: Long)
    fun boolean(name: String, value: Boolean)
}

private fun VariantDimension.buildConfigBlock(): BuildConfigBlock {
    return object : BuildConfigBlock {
        override fun string(name: String, value: String?) {
            buildConfigField("String", name, encodeString(value))
        }

        override fun stringArray(name: String, values: Iterable<String?>) {
            val valueStr = values.joinToString(",") { encodeString(it) }
            buildConfigField("String[]", name, "{$valueStr}")
        }

        override fun int(name: String, value: Int) {
            buildConfigField("int", name, value.toString())
        }

        override fun long(name: String, value: Long) {
            buildConfigField("long", name, value.toString())
        }

        override fun boolean(name: String, value: Boolean) {
            buildConfigField("boolean", name, value.toString())
        }
    }
}
