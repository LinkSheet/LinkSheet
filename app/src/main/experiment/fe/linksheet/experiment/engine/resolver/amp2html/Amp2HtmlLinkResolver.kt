package fe.linksheet.experiment.engine.resolver.amp2html

import fe.linksheet.experiment.engine.resolver.LinkResolver
import fe.linksheet.experiment.engine.resolver.ResolveOutput
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.IResult
import fe.std.result.isFailure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Amp2HtmlLinkResolver(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val source: Amp2HtmlSource,
    private val cacheRepository: CacheRepository,
    private val useLocalCache: () -> Boolean,
) : LinkResolver {
    private suspend fun insertCache(entryId: Long, result: Amp2HtmlResult) {
        cacheRepository.insertResolved(entryId, ResolveType.Amp2Html, result.url)

        if (result is Amp2HtmlResult.NonAmpLink) {
            cacheRepository.insertHtml(entryId, result.htmlText)
        }
    }

    private suspend fun handleResult(
        entryId: Long,
        url: String,
        localCache: Boolean,
        result: IResult<Amp2HtmlResult>
    ): ResolveOutput {
        if (result.isFailure()) {
            return ResolveOutput(url)
        }

        val result = result.value
        if (localCache) {
            insertCache(entryId, result)
        }

        return ResolveOutput(result.url)
    }

    override suspend fun run(data: String): ResolveOutput? = withContext(ioDispatcher) {
        val localCache = useLocalCache()
        val entry = cacheRepository.getOrCreateCacheEntry(data)

        if (localCache) {
            val resolvedUrl = cacheRepository.getResolved(entry.id, ResolveType.Amp2Html)
            if (resolvedUrl != null && resolvedUrl.result != null) {
                return@withContext ResolveOutput(resolvedUrl.result)
            }

            val cachedHtml = cacheRepository.getCachedHtml(entry.id)
            if (cachedHtml != null) {
                val parsedUrlResult = source.parseHtml(cachedHtml.content, data)
                return@withContext handleResult(entry.id, data, true, parsedUrlResult)
            }
        }

        val result = source.resolve(data)
        handleResult(entry.id, data, localCache, result)
    }
}
