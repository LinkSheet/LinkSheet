package fe.linksheet.experiment.engine.fetcher

import fe.linksheet.experiment.engine.fetcher.preview.PreviewFetchResult
import fe.linksheet.experiment.engine.modifier.LibRedirectContextResult

interface ContextResult

sealed interface ContextResultId<out Result : ContextResult> {
    data object LibRedirect : ContextResultId<LibRedirectContextResult>
    data object Download : ContextResultId<DownloadCheckFetchResult>
    data object Preview : ContextResultId<PreviewFetchResult>
}
