package app.linksheet.feature.engine.engine.fetcher

import app.linksheet.feature.engine.engine.fetcher.preview.PreviewFetchResult
import app.linksheet.feature.engine.engine.modifier.LibRedirectContextResult

interface ContextResult

sealed interface ContextResultId<out Result : ContextResult> {
    data object LibRedirect : ContextResultId<LibRedirectContextResult>
    data object Download : ContextResultId<DownloadCheckFetchResult>
    data object Preview : ContextResultId<PreviewFetchResult>
}
