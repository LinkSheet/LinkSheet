package fe.linksheet.experiment.engine.context

import fe.std.extension.emptyEnumSet
import java.util.EnumSet

interface EngineRunContext {
    val extras: Set<EngineExtra>
    val flags: EnumSet<EngineFlag>
}

class DefaultEngineRunContext(override val extras: Set<EngineExtra>) : EngineRunContext {
    override val flags: EnumSet<EngineFlag> = emptyEnumSet()
}

@Suppress("FunctionName")
fun DefaultEngineRunContext(vararg extras: EngineExtra): EngineRunContext {
    return DefaultEngineRunContext(extras = extras.toSet())
}

inline fun <reified E : EngineExtra> EngineRunContext.findExtraOrNull(): E? {
    return extras.filterIsInstance<E>().firstOrNull()
}

enum class EngineFlag {
    DisablePreview
}

sealed interface EngineExtra {

}

data class SourceAppExtra(val appPackage: String) : EngineExtra
