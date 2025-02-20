package fe.linksheet.module.resolver

import androidx.annotation.StringRes
import fe.linksheet.R
import fe.linksheet.util.StringResHolder

enum class ResolveEvent(@StringRes override val id: Int) : StringResHolder {
    Idle(R.string.resolve_event__idle),
    Initialized(R.string.resolve_event__initialized),
    QueryingBrowsers(R.string.resolve_event__querying_browser_list),
    ApplyingLinkModifiers(R.string.resolve_event__applying_link_modifiers),
    ResolvingRedirects(R.string.resolve_event__resolving_redirects),
    RunningAmp2Html(R.string.resolve_event__unamping_link),
    CheckingLibRedirect(R.string.resolve_event__running_libredirect),
    CheckingDownloader(R.string.resolve_event__download_check),
    LoadingPreferredApps(R.string.resolve_event__loading_preferred_apps),
    CheckingBrowsers(R.string.resolve_event__checking_browsers),
    SortingApps(R.string.resolve_event__sorting_apps),
    GeneratingPreview(R.string.resolve_event__generating_preview);

    fun isAtLeast(state: ResolveEvent): Boolean {
        return compareTo(state) >= 0
    }
}

sealed interface ResolverInteraction {
    data object Idle : ResolverInteraction
    data object Initialized : ResolverInteraction
    data object Clear : ResolverInteraction
    data class Cancelable(val event: ResolveEvent, val cancel: () -> Unit) : ResolverInteraction
}
