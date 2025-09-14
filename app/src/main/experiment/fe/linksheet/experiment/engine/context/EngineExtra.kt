package fe.linksheet.experiment.engine.context

import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.util.AndroidAppPackage
import kotlin.reflect.KClass

sealed interface EngineExtra
data class SourceAppExtra(val appPackage: String) : EngineExtra
data class KnownBrowserExtra(val knownBrowser: KnownBrowser) : EngineExtra
data object IgnoreLibRedirectExtra : EngineExtra
data object SkipFollowRedirectsExtra : EngineExtra

fun AndroidAppPackage.toExtra(): SourceAppExtra {
    return SourceAppExtra(packageName)
}

fun KnownBrowser.toExtra(): KnownBrowserExtra {
    return KnownBrowserExtra(this)
}

fun EngineRunContext.findExtraOrNull(extra: KClass<out EngineExtra>): EngineExtra? {
    return extras.filterIsInstance(extra.java).firstOrNull()
}
fun EngineRunContext.hasExtra(extra: KClass<out EngineExtra>): Boolean {
    return findExtraOrNull(extra) != null
}

inline fun <reified E : EngineExtra> EngineRunContext.findExtraOrNull(): E? {
    return findExtraOrNull(E::class) as? E
}

inline fun <reified E : EngineExtra> EngineRunContext.hasExtra(): Boolean {
    return hasExtra(E::class)
}
