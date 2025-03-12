package fe.linksheet.experiment.engine.resolver.followredirects

import android.net.Uri
import fe.fastforwardkt.FastForward
import fe.linksheet.experiment.engine.resolver.LinkResolver
import fe.linksheet.experiment.engine.resolver.ResolveInput
import fe.linksheet.experiment.engine.resolver.ResolveOutput
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.util.web.Darknet
import fe.std.result.isFailure


class FollowRedirectsLinkResolver(
    private val source: FollowRedirectsSource,
    private val cacheRepository: CacheRepository,
    private val isDarknet: (Uri) -> Boolean = { Darknet.getOrNull(it) != null },
    private val isTracker: (String) -> Boolean= { FastForward.isTracker(it) },
    private val allowDarknets: () -> Boolean,
    private val followOnlyKnownTrackers: () -> Boolean,
    private val useLocalCache: () -> Boolean,
) : LinkResolver {

    private suspend fun insertCache(entryId: Long, result: FollowRedirectsResult) {
        cacheRepository.insertResolved(entryId, ResolveType.FollowRedirects, result.url)

        if (result is FollowRedirectsResult.GetRequest && result.body != null) {
            cacheRepository.insertHtml(entryId, result.body)
        }
    }

    override suspend fun resolve(data: ResolveInput): ResolveOutput? {
        val isTracker = isTracker(data.url)
        if(followOnlyKnownTrackers() && !isTracker) {
            return ResolveOutput(data.url)
        }

        val isDarknet = isDarknet(data.uri)
        if (!allowDarknets() && isDarknet) {
            return ResolveOutput(data.url)
        }

        val localCache = useLocalCache()
        val entry = cacheRepository.getOrCreateCacheEntry(data.url)

        if (localCache) {
            val resolvedUrl = cacheRepository.getResolved(entry.id, ResolveType.FollowRedirects)
            if (resolvedUrl != null && resolvedUrl.result != null) {
                return ResolveOutput(resolvedUrl.result)
            }
        }

        val result = source.resolve(data.url)
        if (result.isFailure()) {
            return ResolveOutput(data.url)
        }

        if (localCache) {
            insertCache(entry.id, result.value)
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
