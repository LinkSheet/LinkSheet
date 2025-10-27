package app.linksheet.feature.engine.engine.resolver.followredirects

import app.linksheet.feature.engine.database.entity.cache.ResolveType
import app.linksheet.feature.engine.database.repository.CacheRepository
import app.linksheet.feature.engine.engine.context.EngineRunContext
import app.linksheet.feature.engine.engine.context.SkipFollowRedirectsExtra
import app.linksheet.feature.engine.engine.context.hasExtra
import app.linksheet.feature.engine.engine.resolver.LinkResolver
import app.linksheet.feature.engine.engine.resolver.ResolveOutput
import app.linksheet.feature.engine.engine.resolver.UrlChecker
import app.linksheet.feature.engine.engine.step.EngineStepId
import fe.fastforwardkt.FastForward
import fe.std.result.isFailure
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FollowRedirectsLinkResolver(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val source: FollowRedirectsSource,
    private val cacheRepository: CacheRepository,
    private val isTracker: (String) -> Boolean = { FastForward.isTracker(it) },
    private val allowDarknets: () -> Boolean,
    private val allowNonPublic: () -> Boolean,
    private val urlChecker: UrlChecker = UrlChecker(allowDarknets, allowNonPublic),
    private val followOnlyKnownTrackers: () -> Boolean,
    private val useLocalCache: () -> Boolean,
    override val enabled: () -> Boolean,
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

        urlChecker.check(url)?.let { return@withContext it }

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
