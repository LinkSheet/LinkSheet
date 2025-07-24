package fe.linksheet.experiment.engine.context

import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.util.AndroidAppPackage

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

inline fun <reified E : EngineExtra> EngineRunContext.findExtraOrNull(): E? {
    return extras.filterIsInstance<E>().firstOrNull()
}

inline fun <reified E : EngineExtra> EngineRunContext.hasExtra(): Boolean {
    return findExtraOrNull<E>() != null
}
