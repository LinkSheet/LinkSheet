package fe.linksheet.experiment.engine.resolver.followredirects

import android.net.Uri
import fe.fastforwardkt.FastForward
import fe.linksheet.experiment.engine.resolver.LinkResolver
import fe.linksheet.experiment.engine.resolver.ResolveOutput
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.util.web.Darknet
import fe.std.result.isFailure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FollowRedirectsLinkResolver(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val source: FollowRedirectsSource,
    private val cacheRepository: CacheRepository,
    private val isDarknet: (Uri) -> Boolean = { Darknet.getOrNull(it) != null },
    private val isTracker: (String) -> Boolean = { FastForward.isTracker(it) },
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

    override suspend fun run(url: String): ResolveOutput? = withContext(ioDispatcher) {
        val isTracker = isTracker(url)
        if (followOnlyKnownTrackers() && !isTracker) {
            return@withContext ResolveOutput(url)
        }

        val uri = Uri.parse(url)
        val isDarknet = isDarknet(uri)
        if (!allowDarknets() && isDarknet) {
            return@withContext ResolveOutput(url)
        }

        val localCache = useLocalCache()
        val entry = cacheRepository.getOrCreateCacheEntry(url)

        if (localCache) {
            val resolvedUrl = cacheRepository.getResolved(entry.id, ResolveType.FollowRedirects)
            if (resolvedUrl != null && resolvedUrl.result != null) {
                return@withContext ResolveOutput(resolvedUrl.result)
            }
        }

        val result = source.resolve(url)
        if (result.isFailure()) {
            return@withContext ResolveOutput(url)
        }

        if (localCache) {
            insertCache(entry.id, result.value)
        }

        ResolveOutput(result.value.url)
    }
}
