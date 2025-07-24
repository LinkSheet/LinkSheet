package fe.linksheet.experiment.engine.resolver.followredirects

import fe.fastforwardkt.FastForward
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.context.SkipFollowRedirectsExtra
import fe.linksheet.experiment.engine.context.hasExtra
import fe.linksheet.experiment.engine.resolver.LinkResolver
import fe.linksheet.experiment.engine.resolver.ResolveOutput
import fe.linksheet.experiment.engine.resolver.UriChecker
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.isFailure
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


data class FollowRedirectsLinkResolver(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val source: FollowRedirectsSource,
    private val cacheRepository: CacheRepository,
    private val isTracker: (String) -> Boolean = { FastForward.isTracker(it) },
    private val allowDarknets: () -> Boolean,
    private val allowNonPublic: () -> Boolean,
    private val uriChecker: UriChecker = UriChecker(allowDarknets, allowNonPublic),
    private val followOnlyKnownTrackers: () -> Boolean,
    private val useLocalCache: () -> Boolean,
) : LinkResolver {
    override val id = EngineStepId.FollowRedirects

    private suspend fun insertCache(entryId: Long, result: FollowRedirectsResult) {
        cacheRepository.insertResolved(entryId, ResolveType.FollowRedirects, result.url)

        if (result is FollowRedirectsResult.GetRequest && result.body != null) {
            cacheRepository.insertHtml(entryId, result.body)
        }
    }

    override suspend fun EngineRunContext.runStep(url: StdUrl): ResolveOutput? = withContext(ioDispatcher) {
        val urlString = url.toString()
        if (hasExtra<SkipFollowRedirectsExtra>()) {
            return@withContext ResolveOutput(url)
        }

        val isTracker = isTracker(urlString)
        if (followOnlyKnownTrackers() && !isTracker) {
            return@withContext ResolveOutput(url)
        }

        uriChecker.check(url)?.let { return@withContext it }

        val localCache = useLocalCache()
        val entry = cacheRepository.getOrCreateCacheEntry(urlString)

        if (localCache) {
            val resolvedUrl = cacheRepository.getResolved(entry.id, ResolveType.FollowRedirects)
            if (resolvedUrl != null && resolvedUrl.result != null) {
                return@withContext ResolveOutput(resolvedUrl.result.toStdUrlOrThrow())
            }
        }

        val result = source.resolve(urlString)
        if (result.isFailure()) {
            return@withContext ResolveOutput(url)
        }

        if (localCache) {
            insertCache(entry.id, result.value)
        }

        ResolveOutput(result.value.url.toStdUrlOrThrow())
    }
}
