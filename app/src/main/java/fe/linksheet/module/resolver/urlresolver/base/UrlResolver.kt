package fe.linksheet.module.resolver.urlresolver.base

import android.net.Uri
import android.net.compatHost
import app.linksheet.feature.engine.database.entity.ResolveType
import app.linksheet.feature.engine.database.repository.CacheRepository
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.extension.kotlin.unwrapOrNull
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import fe.linksheet.module.database.entity.resolver.ResolverEntity
import fe.linksheet.module.log.internal.LoggerDelegate
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.repository.resolver.ResolverRepository
import fe.linksheet.module.resolver.urlresolver.RemoteResolveRequest
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlResolveRequest
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectResolveRequest
import fe.linksheet.web.Darknet
import fe.linksheet.web.HostType
import fe.linksheet.web.HostUtil
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.koin.core.component.KoinComponent
import kotlin.reflect.KClass

typealias ResolvePredicate = (Uri) -> Boolean
typealias RedirectUrlResolver = UrlResolver<ResolvedRedirect, RedirectResolveRequest, ResolvedRedirectRepository>
typealias Amp2HtmlUrlResolver = UrlResolver<Amp2HtmlMapping, Amp2HtmlResolveRequest, Amp2HtmlRepository>

class UrlResolver<T : ResolverEntity<T>, L : LocalResolveRequest, R : ResolverRepository<T>>(
    clazz: KClass<L>,
    private val localRequest: L,
    private val remoteRequest: RemoteResolveRequest,
    private val resolverRepository: R,
    private val cacheRepository: CacheRepository,
) : KoinComponent {
    private val logger by injectLogger(clazz)

    suspend fun resolve(
        uri: Uri,
        localCache: Boolean,
        resolvePredicate: ResolvePredicate? = null,
        externalService: Boolean,
        connectTimeout: Int,
        canAccessInternet: Boolean,
        allowDarknets: Boolean,
        allowLocalNetwork: Boolean,
        resolveType: ResolveType? = null
    ): Result<ResolveResultType>? {
        currentCoroutineContext().ensureActive()
        if (resolvePredicate?.invoke(uri) == false) {
            return null
        }

        val darknet = Darknet.getOrNull(uri)
        val isPublicHost = HostUtil.getHostType(uri.compatHost) == HostType.Host

        val uriString = uri.toString()
        val uriLogContext = logger.createContext(uriString, HashProcessor.UrlProcessor)

        if (!allowDarknets && darknet != null) {
            logger.info(uriLogContext) { "$it is a darknet url, but darknets are not allowed, skipping" }
            return null
        }

        if (!allowLocalNetwork && !isPublicHost) {
            logger.info(uriLogContext) { "$it is not a public url, but local networks are not allowed, skipping" }
            return null
        }

        if (localCache) {
            if (resolveType != null) {
                val cacheData = cacheRepository.checkCache(uriString, resolveType)
                if (cacheData != null) {
                    val resolved = cacheData.resolved ?: uriString

                    logger.info(resolved, HashProcessor.UrlProcessor) { "From local cache: $it" }
                    return ResolveResultType.Resolved.LocalCache(resolved).success()
                }
            }

            val entry = resolverRepository.getForInputUrl(uriString)
            if (entry != null) {
                val cachedUrl = entry.url ?: return null

                logger.info(cachedUrl, HashProcessor.UrlProcessor) { "From local cache: $it" }
                return ResolveResultType.Resolved.LocalCache(cachedUrl).success()
            }
        }

        if (!canAccessInternet) {
            return ResolveResultType.NoInternetConnection.success()
        }

        val resolveResult = resolve(uriString, uriLogContext, externalService, darknet, isPublicHost, connectTimeout)

        if (localCache) {
            // TODO: Insert skip
            val url = resolveResult.unwrapOrNull<ResolveResultType, ResolveResultType.Resolved>()?.url
            if (url != null) {
                resolverRepository.insert(uriString, url)
            }
        }

        return resolveResult
    }

    private fun canResolveExternally(
        logContext: LoggerDelegate.RedactedParameter,
        darknet: Darknet?,
        isPublicHost: Boolean,
    ): Boolean {
        if (darknet != null) {
            logger.info(logContext) { "$it is a darknet url, but external services are enabled, skipping" }
            return false
        }

        if (!isPublicHost) {
            logger.info(logContext) { "$it is not publicly accessible, falling back to local resolving" }
            return false
        }

        return true
    }

    private suspend fun resolve(
        uriString: String,
        logContext: LoggerDelegate.RedactedParameter,
        externalService: Boolean,
        darknet: Darknet?,
        isPublicHost: Boolean,
        timeout: Int,
    ): Result<ResolveResultType> {
        if (externalService && canResolveExternally(logContext, darknet, isPublicHost)) {
            logger.info(logContext) { "Attempting to resolve $it via external service.." }
            val result = remoteRequest.resolveRemote(uriString, timeout, resolverRepository.remoteResolveUrlField)
            if (result.isFailure) logger.error("External resolve failed", result.exceptionOrNull())

            return result
        }

        logger.info(logContext) { "Using local service for $it" }
        val result = localRequest.resolveLocal(uriString, timeout)
        if (result.isFailure) logger.error("Local service resolve failed", result.exceptionOrNull())

        return result
    }
}
