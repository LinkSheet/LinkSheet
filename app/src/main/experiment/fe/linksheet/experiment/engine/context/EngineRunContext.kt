package fe.linksheet.experiment.engine.context

import fe.linksheet.experiment.engine.fetcher.FetchResult
import fe.linksheet.experiment.engine.fetcher.LinkFetcherId
import fe.linksheet.module.resolver.util.AndroidAppPackage
import fe.std.extension.emptyEnumSet
import java.util.EnumSet

interface EngineRunContext {
    val extras: Set<EngineExtra>
    val flags: EnumSet<EngineFlag>

    fun put(id: LinkFetcherId, result: FetchResult?)
    fun confirm(fetcher: LinkFetcherId): Boolean
}

class DefaultEngineRunContext(override val extras: Set<EngineExtra>) : EngineRunContext {
    override val flags: EnumSet<EngineFlag> = emptyEnumSet()
    private val results = mutableMapOf<LinkFetcherId, FetchResult?>()

    override fun put(id: LinkFetcherId, result: FetchResult?) {
        results[id] = result
    }

    override fun confirm(fetcher: LinkFetcherId): Boolean {
        return when (fetcher) {
            LinkFetcherId.Download -> true
            LinkFetcherId.Preview -> EngineFlag.DisablePreview !in flags
        }
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
