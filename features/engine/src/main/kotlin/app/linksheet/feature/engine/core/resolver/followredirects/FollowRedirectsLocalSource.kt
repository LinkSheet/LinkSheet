package app.linksheet.feature.engine.core.resolver.followredirects

import app.linksheet.feature.engine.core.resolver.configureHeaders
import fe.linksheet.web.RefreshParser
import fe.linksheet.extension.ktor.isHtml
import fe.linksheet.extension.ktor.refresh
import fe.linksheet.extension.ktor.urlString
import fe.linksheet.web.RefreshHeader
import fe.std.result.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jsoup.Jsoup


class FollowRedirectsLocalSource(private val client: HttpClient) : FollowRedirectsSource {

    override suspend fun resolve(urlString: String): IResult<FollowRedirectsResult> {
        val headResult = tryCatch {
            client.head(urlString = urlString) { configureHeaders() }
        }
        if (headResult.isFailure()) {
            return +headResult
        }

        val headResponse = headResult.value
        val headRefreshHeaderUrl = headResponse.handleRefreshHeader()
        if (headRefreshHeaderUrl != null) {
            return +FollowRedirectsResult.RefreshHeader(headRefreshHeaderUrl)
        }

        // TODO: Decide what to do
//        headResponse.headers["location"] != null &&
        if (headResponse.status.value !in 400..499) {
            return +FollowRedirectsResult.LocationHeader(headResponse.urlString())
        }

        val getResult = tryCatch {
            client.get(urlString = urlString) { configureHeaders() }
        }
        if (getResult.isFailure()) {
            return +getResult
        }

        val getResponse = getResult.value
        val getRefreshHeaderUrl = getResponse.handleRefreshHeader()
        if (getRefreshHeaderUrl != null) {
            return +FollowRedirectsResult.RefreshHeader(getRefreshHeaderUrl)
        }

        val getUrlString = getResponse.urlString()
        val htmlText = if (getResponse.isHtml()) getResponse.bodyAsText() else null
        if (htmlText != null) {
            val getMetaRefreshUrl = handleRefreshMeta(htmlText, getUrlString)
            if (getMetaRefreshUrl != null) {
                return +FollowRedirectsResult.RefreshMeta(getMetaRefreshUrl, htmlText)
            }
        }

        return +FollowRedirectsResult.GetRequest(getUrlString, htmlText)
    }

    private fun handleRefreshMeta(htmlText: String, urlString: String): String? {
        return tryCatch { RefreshParser.parseHtml(Jsoup.parse(htmlText, urlString))?.isValid() }.getOrNull()
    }

    private fun HttpResponse.handleRefreshHeader(): String? {
        return refresh()?.let { RefreshParser.parseRefreshHeader(it) }?.isValid()
    }

    private fun RefreshHeader.isValid(): String? {
        val (delay, refreshUrl) = this
        if (delay != 0) return null
        // Check if parsable
        parseUrl(refreshUrl) ?: return null
        return refreshUrl
    }
}
