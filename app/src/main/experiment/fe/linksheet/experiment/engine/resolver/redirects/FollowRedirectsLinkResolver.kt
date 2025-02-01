package fe.linksheet.experiment.engine.resolver.redirects

import fe.linksheet.experiment.engine.resolver.LinkResolver
import fe.linksheet.experiment.engine.resolver.ResolveInput
import fe.linksheet.experiment.engine.resolver.ResolveOutput
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.util.web.Darknet
import fe.std.result.isSuccess
import io.ktor.client.HttpClient
import org.koin.core.component.KoinComponent


class FollowRedirectsLinkResolver(
    private val source: FollowRedirectsSource,
    private val cacheRepository: CacheRepository,
    private val allowDarknets: () -> Boolean,
    private val localCache: () -> Boolean,
) : LinkResolver, KoinComponent {

    private suspend fun insertCache(url: String, result: FollowRedirectsResult) {
        val entry = cacheRepository.createUrlEntry(url)
        cacheRepository.insertResolved(entry, ResolveType.FollowRedirects, result.url)

        if (result is FollowRedirectsResult.GetRequest) {
            cacheRepository.insertHtml(entry, result.body)
        }
    }

    override suspend fun resolve(data: ResolveInput): ResolveOutput? {
        val darknet = Darknet.getOrNull(data.uri)
        if (!allowDarknets() && darknet != null) {
            return ResolveOutput(data.url)
        }

        val localCache = localCache()
        if (localCache) {
            val cacheData = cacheRepository.checkCache(data.url, ResolveType.FollowRedirects)
            if (cacheData != null) {
                val resolved = cacheData.resolved ?: data.url
                return ResolveOutput(resolved)
            }
        }

        val result = source.resolve(data.url)
        if (!result.isSuccess()) {
            return ResolveOutput(data.url)
        }

        if (localCache) {
            insertCache(data.url, result.value)
        }

        return ResolveOutput(result.value.url)
    }

//    private suspend fun runRedirectResolver(
//        dispatcher: CoroutineDispatcher = Dispatchers.IO,
//        resolveModuleStatus: ResolveModuleStatus,
//        redirectResolver: RedirectUrlResolver,
//        uri: Uri,
//        canAccessInternet: Boolean = true,
//        requestTimeout: Int,
//        followRedirects: Boolean,
//        followRedirectsExternalService: Boolean,
//        followOnlyKnownTrackers: Boolean,
//        followRedirectsLocalCache: Boolean,
//        followRedirectsAllowDarknets: Boolean,
//    ): Uri? = withContext(dispatcher) {
//        logger.debug("Executing runRedirectResolver on ${Thread.currentThread().name}")
//
//        resolveModuleStatus.resolveIfEnabled(followRedirects, ResolveModule.Redirect, uri) { uriToResolve ->
//            logger.debug("Inside redirect func, on ${Thread.currentThread().name}")
//
//            val resolvePredicate: ResolvePredicate = { uri ->
//                (!followRedirectsExternalService && !followOnlyKnownTrackers) || FastForward.isTracker(uri.toString())
//            }
//
//            redirectResolver.resolve(
//                uriToResolve,
//                followRedirectsLocalCache,
//                resolvePredicate,
//                followRedirectsExternalService,
//                requestTimeout,
//                canAccessInternet,
//                followRedirectsAllowDarknets
//            )
//        }
//    }
}
