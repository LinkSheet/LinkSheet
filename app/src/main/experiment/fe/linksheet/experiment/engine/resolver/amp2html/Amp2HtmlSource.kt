package fe.linksheet.experiment.engine.resolver.amp2html

import fe.std.result.IResult

interface Amp2HtmlSource {
    suspend fun resolve(urlString: String): IResult<Amp2HtmlResult>
    suspend fun parseHtml(htmlText: String, urlString: String): IResult<Amp2HtmlResult>
}

sealed class Amp2HtmlResult(val url: String) {
    class NothingToResolve(url: String) : Amp2HtmlResult(url)
    class NonAmpLink(url: String, val htmlText: String) : Amp2HtmlResult(url)
}
