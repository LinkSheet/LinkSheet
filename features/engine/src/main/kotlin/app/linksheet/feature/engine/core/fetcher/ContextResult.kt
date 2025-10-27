package app.linksheet.feature.engine.core.fetcher

import app.linksheet.feature.engine.core.fetcher.preview.PreviewFetchResult
import app.linksheet.feature.engine.core.modifier.LibRedirectContextResult

interface ContextResult

sealed interface ContextResultId<out Result : ContextResult> {
    data object LibRedirect : ContextResultId<LibRedirectContextResult>
    data object Download : ContextResultId<DownloadCheckFetchResult>
    data object Preview : ContextResultId<PreviewFetchResult>
}
