package fe.linksheet.experiment.engine.context

import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.fetcher.ContextResult
import fe.linksheet.experiment.engine.fetcher.ContextResultId
import fe.linksheet.experiment.engine.slot.AppRoleId
import fe.linksheet.util.AndroidAppPackage
import fe.std.extension.emptyEnumSet
import java.util.EnumSet

interface EngineRunContext {
    val extras: Set<EngineExtra>
    val flags: EnumSet<EngineFlag>
    val roles: MutableMap<AppRoleId, AndroidAppPackage>

    fun <Result : ContextResult> put(id: ContextResultId<Result>, result: Result?)
    fun <Result : ContextResult> confirm(fetcher: ContextResultId<Result>): Boolean
    fun seal(): SealedRunContext

    fun empty(): EngineResult? = null
}

class DefaultEngineRunContext(override val extras: Set<EngineExtra>) : EngineRunContext {
    override val flags: EnumSet<EngineFlag> = emptyEnumSet()
    override val roles: MutableMap<AppRoleId, AndroidAppPackage> = mutableMapOf()
    private val results = mutableMapOf<ContextResultId<*>, ContextResult?>()

    // TODO: Consider splitting into input data class, runtime context and SealedRunContext to avoid allowing mutations of [results]
    // from outside a LinkEngine run
    override fun <Result : ContextResult> put(id: ContextResultId<Result>, result: Result?) {
        results[id] = result
    }

    // TODO: [ContextResultId.LibRedirect] works different than the other results, as it is obtained and stored in the
    //  context via [LibRedirectLinkModifier] instead of being obtained from within [LinkEngine];
    //  Is this fine? Do we need to add another level of abstraction?
    override fun <Result : ContextResult> confirm(fetcher: ContextResultId<Result>): Boolean {
        return when (fetcher) {
            ContextResultId.Download -> true
            ContextResultId.Preview -> EngineFlag.DisablePreview !in flags
            ContextResultId.LibRedirect -> false
        }
    }

    override fun seal(): SealedRunContext {
        return SealedRunContext(flags, roles, results)
    }
}

data class SealedRunContext(
    val flags: Set<EngineFlag>,
    val roles: Map<AppRoleId, AndroidAppPackage>,
    private val results: Map<ContextResultId<*>, ContextResult?>
) {

    operator fun <Result : ContextResult> get(id: ContextResultId<Result>): Result? {
        @Suppress("UNCHECKED_CAST")
        return results[id] as Result?
    }
}

@Suppress("FunctionName")
fun DefaultEngineRunContext(vararg extras: EngineExtra?): EngineRunContext {
    return DefaultEngineRunContext(extras = extras.filterNotNull().toSet())
}

@Suppress("FunctionName")
fun DefaultEngineRunContext(builderAction: MutableSet<EngineExtra>.() -> Unit): EngineRunContext {
    return DefaultEngineRunContext(buildSet(builderAction))
}

inline fun <reified E : EngineExtra> EngineRunContext.findExtraOrNull(): E? {
    return extras.filterIsInstance<E>().firstOrNull()
}

inline fun <reified E : EngineExtra> EngineRunContext.hasExtra(): Boolean {
    return findExtraOrNull<E>() != null
}

enum class EngineFlag {
    DisablePreview
}

sealed interface EngineExtra
data class SourceAppExtra(val appPackage: String) : EngineExtra
data object IgnoreLibRedirectExtra : EngineExtra

fun AndroidAppPackage.toSourceAppExtra(): SourceAppExtra {
    return SourceAppExtra(packageName)
}
