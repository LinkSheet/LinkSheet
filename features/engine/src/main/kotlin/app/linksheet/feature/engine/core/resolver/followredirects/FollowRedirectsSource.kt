package app.linksheet.feature.engine.core.resolver.followredirects

import fe.std.result.IResult

fun interface FollowRedirectsSource {
    suspend fun resolve(urlString: String): IResult<FollowRedirectsResult>
}

sealed class FollowRedirectsResult(val url: String, val body: String?) {
    class RefreshHeader(url: String) : FollowRedirectsResult(url, null)
    class LocationHeader(url: String) : FollowRedirectsResult(url, null)
    class RefreshMeta(url: String, body: String?) : FollowRedirectsResult(url, body)
    class GetRequest(url: String, body: String?) : FollowRedirectsResult(url, body)
}
