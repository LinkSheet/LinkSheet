package fe.linksheet.experiment.engine.context

import fe.linksheet.experiment.engine.fetcher.FetchResult
import fe.linksheet.experiment.engine.fetcher.LinkFetcherId
import fe.linksheet.module.resolver.util.AndroidAppPackage
import fe.std.extension.emptyEnumSet
import java.util.EnumSet

interface EngineRunContext {
    val extras: Set<EngineExtra>
    val flags: EnumSet<EngineFlag>

    fun <Result : FetchResult> put(id: LinkFetcherId<Result>, result: Result?)
    fun <Result : FetchResult> confirm(fetcher: LinkFetcherId<Result>): Boolean
    fun seal(): SealedRunContext
}

class DefaultEngineRunContext(override val extras: Set<EngineExtra>) : EngineRunContext {
    override val flags: EnumSet<EngineFlag> = emptyEnumSet()
    private val results = mutableMapOf<LinkFetcherId<*>, FetchResult?>()

    // TODO: Consider splitting into input data class, runtime context and SealedRunContext to avoid allowing mutations of [results]
    // from outside a LinkEngine run
    override fun <Result : FetchResult> put(id: LinkFetcherId<Result>, result: Result?) {
        results[id] = result
    }

    override fun <Result : FetchResult> confirm(fetcher: LinkFetcherId<Result>): Boolean {
        return when (fetcher) {
            LinkFetcherId.Download -> true
            LinkFetcherId.Preview -> EngineFlag.DisablePreview !in flags
        }
    }

    override fun seal(): SealedRunContext {
        return SealedRunContext(flags, results)
    }
}

data class SealedRunContext(val flags: Set<EngineFlag>, private val results: Map<LinkFetcherId<*>, FetchResult?>) {
    fun <Result : FetchResult> get(id: LinkFetcherId<Result>): Result? {
        @Suppress("UNCHECKED_CAST")
        return results[id] as Result?
    }
}

@Suppress("FunctionName")
fun DefaultEngineRunContext(vararg extras: EngineExtra?): EngineRunContext {
    return DefaultEngineRunContext(extras = extras.filterNotNull().toSet())
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

fun AndroidAppPackage.toSourceAppExtra(): SourceAppExtra {
    return SourceAppExtra(packageName)
}
