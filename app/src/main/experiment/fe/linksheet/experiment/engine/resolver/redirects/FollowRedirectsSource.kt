package fe.linksheet.experiment.engine.resolver.redirects

import fe.std.result.IResult

fun interface FollowRedirectsSource {
    suspend fun resolve(urlString: String): IResult<FollowRedirectsResult>
}
