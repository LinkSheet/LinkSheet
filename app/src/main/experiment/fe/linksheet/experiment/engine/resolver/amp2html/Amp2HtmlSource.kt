package fe.linksheet.experiment.engine.resolver.amp2html

import fe.std.result.IResult
import fe.std.uri.StdUrl

interface Amp2HtmlSource {
    suspend fun resolve(urlString: String): IResult<Amp2HtmlResult>
    suspend fun parseHtml(htmlText: String, urlString: String): IResult<Amp2HtmlResult>
}

sealed class Amp2HtmlResult(val url: StdUrl) {
    class NothingToResolve(url: StdUrl) : Amp2HtmlResult(url)
    class NonAmpLink(url: StdUrl, val htmlText: String) : Amp2HtmlResult(url)
}
