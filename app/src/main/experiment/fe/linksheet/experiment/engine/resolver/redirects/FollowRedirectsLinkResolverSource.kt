package fe.linksheet.experiment.engine.resolver.redirects

import fe.linksheet.experiment.engine.DataSource
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectResolveRequest.Companion.parseRefreshHeader
import fe.std.result.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class FollowRedirectsLinkResolverSource : DataSource {
    override suspend fun compute() {

    }
}

suspend fun resolveLocal(client: HttpClient, url: Url): IResult<ResolveResultType> {
    val headResult = tryCatch { client.head(url) }
    if (headResult.isFailure()) {
        return headResult.cast()
    }

    val headResponse by headResult
    val refreshHeaderUrl = headResponse.handleRefreshHeader()
    if (refreshHeaderUrl != null) {
        return Success(ResolveResultType.Resolved.Local(refreshHeaderUrl))
    }

    if (headResponse.status.value !in 400..499) {
        return headResponse.toSuccessResult()
    }

    val getResult = tryCatch { client.get(url) }
    return when (getResult.isSuccess()) {
        true -> getResult.value.toSuccessResult()
        false -> getResult.cast()
    }
}

private fun HttpResponse.handleRefreshHeader(): String? {
    val refreshHeader = headers["refresh"] ?: return null
    val parsedHeader = parseRefreshHeader(refreshHeader) ?: return null

    return parsedHeader
        .takeIf { it.first == 0 }
        ?.takeIf { parseUrl(it.second) != null }
        ?.second
}

private fun HttpResponse.toSuccessResult(): IResult<ResolveResultType> {
    return Success(ResolveResultType.Resolved.Local(request.url.toString()))
}





