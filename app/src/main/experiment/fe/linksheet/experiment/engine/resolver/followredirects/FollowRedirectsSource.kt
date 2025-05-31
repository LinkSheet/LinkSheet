package fe.linksheet.experiment.engine.resolver.followredirects

import fe.std.result.IResult

fun interface FollowRedirectsSource {
    suspend fun resolve(urlString: String): IResult<FollowRedirectsResult>
}

sealed class FollowRedirectsResult(val url: String) {
    class RefreshHeader(url: String) : FollowRedirectsResult(url)
    class LocationHeader(url: String) : FollowRedirectsResult(url)
    class GetRequest(url: String, val body: String?) : FollowRedirectsResult(url)
}
