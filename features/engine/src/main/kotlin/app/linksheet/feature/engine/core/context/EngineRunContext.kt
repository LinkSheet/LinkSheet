package app.linksheet.feature.engine.core.context

import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.fetcher.ContextResult
import app.linksheet.feature.engine.core.fetcher.ContextResultId
import fe.linksheet.util.AndroidAppPackage
import fe.std.extension.emptyEnumSet
import java.util.*

interface EngineRunContext {
    val extras: Set<EngineExtra>
    val flags: EnumSet<EngineFlag>
    val roles: MutableSet<AppRole>
    var allowCustomTab: Boolean?

    fun put(id: AppRoleId, appPackage: AndroidAppPackage): Boolean
    fun <Result : ContextResult> put(id: ContextResultId<Result>, result: Result?)
    fun <Result : ContextResult> confirm(fetcher: ContextResultId<Result>): Boolean
    fun seal(): SealedRunContext

    fun empty(): EngineResult? = null
}

class DefaultEngineRunContext(override val extras: Set<EngineExtra>) : EngineRunContext {
    override val flags: EnumSet<EngineFlag> = emptyEnumSet()
    override val roles: MutableSet<AppRole> = mutableSetOf()
    override var allowCustomTab: Boolean? = null
    private val results = mutableMapOf<ContextResultId<*>, ContextResult?>()

    override fun put(id: AppRoleId, appPackage: AndroidAppPackage): Boolean {
        return roles.add(AppRole(id, appPackage))
    }

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
        return SealedRunContext(flags, roles, allowCustomTab, results)
    }
}

data class SealedRunContext(
    val flags: Set<EngineFlag>,
    val roles: Set<AppRole>,
    val allowCustomTab: Boolean?,
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
