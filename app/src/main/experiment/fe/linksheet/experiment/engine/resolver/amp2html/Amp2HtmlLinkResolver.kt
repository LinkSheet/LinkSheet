package fe.linksheet.experiment.engine.resolver.amp2html

import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.experiment.engine.resolver.LinkResolver
import fe.linksheet.experiment.engine.resolver.ResolveOutput
import fe.linksheet.experiment.engine.resolver.UrlChecker
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.IResult
import fe.std.result.isFailure
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Amp2HtmlLinkResolver(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val source: Amp2HtmlSource,
    private val cacheRepository: CacheRepository,
    private val allowDarknets: () -> Boolean = { false },
    private val allowNonPublic: () -> Boolean = { false },
    private val urlChecker: UrlChecker = UrlChecker(allowDarknets, allowNonPublic),
    private val useLocalCache: () -> Boolean,
    override val enabled: () -> Boolean = { true },
) : LinkResolver {
    override val id = EngineStepId.Amp2Html

    private suspend fun insertCache(entryId: Long, result: Amp2HtmlResult) {
        cacheRepository.insertResolved(entryId, ResolveType.Amp2Html, result.url.toString())

        if (result is Amp2HtmlResult.NonAmpLink) {
            cacheRepository.insertHtml(entryId, result.htmlText)
        }
    }

    private suspend fun handleResult(
        entryId: Long,
        url: StdUrl,
        localCache: Boolean,
        result: IResult<Amp2HtmlResult>,
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

    override suspend fun EngineRunContext.runStep(url: StdUrl): ResolveOutput? = withContext(ioDispatcher) {
        urlChecker.check(url)?.let { return@withContext it }

        val localCache = useLocalCache()
        val entry = cacheRepository.getOrCreateCacheEntry(url.toString())

        if (localCache) {
            val resolvedUrl = cacheRepository.getResolved(entry.id, ResolveType.Amp2Html)
            if (resolvedUrl != null && resolvedUrl.result != null) {
                return@withContext ResolveOutput(resolvedUrl.result.toStdUrlOrThrow())
            }

            val cachedHtml = cacheRepository.getCachedHtml(entry.id)
            if (cachedHtml != null) {
                val parsedUrlResult = source.parseHtml(cachedHtml.content, url)
                return@withContext handleResult(entry.id, url, true, parsedUrlResult)
            }
        }

        val result = source.resolve(url)
        handleResult(entry.id, url, localCache, result)
    }
}
