package fe.linksheet.experiment.engine.resolver.amp2html

import fe.linksheet.experiment.engine.resolver.LinkResolver
import fe.linksheet.experiment.engine.resolver.ResolveInput
import fe.linksheet.experiment.engine.resolver.ResolveOutput
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.IResult
import fe.std.result.isFailure

class Amp2HtmlLinkResolver(
    private val source: Amp2HtmlSource,
    private val cacheRepository: CacheRepository,
    private val localCache: () -> Boolean,
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

    override suspend fun resolve(data: ResolveInput): ResolveOutput? {
        val localCache = localCache()
        val entry = cacheRepository.getOrCreateCacheEntry(data.url)

        if (localCache) {
            val resolvedUrl = cacheRepository.getResolved(entry.id, ResolveType.Amp2Html)
            if (resolvedUrl != null && resolvedUrl.result != null) {
                return ResolveOutput(resolvedUrl.result)
            }

            val cachedHtml = cacheRepository.getCachedHtml(entry.id)
            if (cachedHtml != null) {
                val parsedUrlResult = source.parseHtml(cachedHtml.content, data.url)
                return handleResult(entry.id, data.url, true, parsedUrlResult)
            }
        }

        val result = source.resolve(data.url)
        return handleResult(entry.id, data.url, localCache, result)
    }
}
