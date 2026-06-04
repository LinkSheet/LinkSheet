package fe.linksheet.module.resolver.urlresolver.base

import android.net.Uri
import android.net.compatHost
import app.linksheet.feature.engine.database.entity.ResolveType
import app.linksheet.feature.engine.database.repository.CacheRepository
import fe.composekit.mozilla.components.support.base.log.logger.Logger
import fe.linksheet.extension.kotlin.unwrapOrNull
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import fe.linksheet.module.resolver.urlresolver.RemoteResolver
import fe.linksheet.module.resolver.urlresolver.RemoteTask
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.web.Darknet
import fe.linksheet.web.HostType
import fe.linksheet.web.HostUtil
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.koin.core.component.KoinComponent

typealias ResolvePredicate = (Uri) -> Boolean
typealias GetForInputUrl<T> = (String) -> Pair<T, String?>?
typealias Insert<T> = suspend (inputUrl: String, resolvedUrl: String) -> T

sealed class LocalTask<T>(
    val request: LocalResolveRequest,
    val getForInputUrl: GetForInputUrl<T>,
    val insert: Insert<T>,
    val remoteResolveUrlField: String
) {
    class Amp2Html(
        request: LocalResolveRequest,
        getForInputUrl: GetForInputUrl<Amp2HtmlMapping>,
        insert: Insert<Amp2HtmlMapping>
    ) : LocalTask<Amp2HtmlMapping>(request, getForInputUrl, insert, "canonicalUrl")

    class Redirector(
        request: LocalResolveRequest,
        getForInputUrl: GetForInputUrl<ResolvedRedirect>,
        insert: Insert<ResolvedRedirect>
    ) : LocalTask<ResolvedRedirect>(request, getForInputUrl, insert, "resolvedUrl")
}

class UrlResolver(
    private val redirectorTask: LocalTask.Redirector,
    private val aggressiveRedirectorTask: LocalTask.Redirector,
    private val amp2HtmlTask: LocalTask.Amp2Html,
    private val remoteResolver: RemoteResolver,
    private val cacheRepository: CacheRepository,
) : KoinComponent {
    private val logger = Logger("UrlResolver")
    suspend fun resolveRedirect(
        uri: Uri,
        localCache: Boolean,
        resolvePredicate: ResolvePredicate? = null,
        aggressive: Boolean,
        externalService: Boolean,
        connectTimeout: Int,
        canAccessInternet: Boolean,
        allowDarknets: Boolean,
        allowLocalNetwork: Boolean,
    ): Result<ResolveResultType>? {
        val task = if (aggressive) aggressiveRedirectorTask else redirectorTask
        return resolve(
            task,
            uri,
            localCache,
            resolvePredicate,
            externalService,
            connectTimeout,
            canAccessInternet,
            allowDarknets,
            allowLocalNetwork,
            ResolveType.FollowRedirects
        )
    }

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
        return when (resolveType) {
            ResolveType.Amp2Html -> {
                resolve(
                    amp2HtmlTask,
                    uri, localCache, resolvePredicate, externalService,
                    connectTimeout, canAccessInternet, allowDarknets, allowLocalNetwork, resolveType
                )
            }

            ResolveType.FollowRedirects -> {
                resolve(
                    redirectorTask,
                    uri,
                    localCache,
                    resolvePredicate,
                    externalService,
                    connectTimeout,
                    canAccessInternet,
                    allowDarknets,
                    allowLocalNetwork,
                    resolveType
                )
            }

            else -> null
        }
    }

    private suspend fun <T> resolve(
        task: LocalTask<T>,
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
//        val uriLogContext = logger.createContext(uriString, HashProcessor.UrlProcessor)

        if (!allowDarknets && darknet != null) {
            logger.debug("$uriString is a darknet url, but darknets are not allowed, skipping")
            return null
        }

        if (!allowLocalNetwork && !isPublicHost) {
            logger.debug("${uriString} is not a public url, but local networks are not allowed, skipping")
            return null
        }

        if (localCache) {
            if (resolveType != null) {
                val cacheData = cacheRepository.checkCache(uriString, resolveType)
                if (cacheData != null) {
                    val resolved = cacheData.resolved ?: uriString

                    logger.debug("From local cache: $resolved")
                    return ResolveResultType.Resolved.LocalCache(resolved).success()
                }
            }

            val result = task.getForInputUrl(uriString)
            if (result != null) {
                val (_, url) = result
                val cachedUrl = url ?: return null

                logger.debug("From local cache: $cachedUrl")
                return ResolveResultType.Resolved.LocalCache(cachedUrl).success()
            }
        }

        if (!canAccessInternet) {
            return ResolveResultType.NoInternetConnection.success()
        }

        val resolveResult = resolve(
            localTask = task,
            resolveType = resolveType,
            uriString = uriString,
            externalService = externalService,
            darknet = darknet,
            isPublicHost = isPublicHost,
            timeout = connectTimeout
        )

        if (localCache) {
            // TODO: Insert skip
            val url =
                resolveResult.unwrapOrNull<ResolveResultType, ResolveResultType.Resolved>()?.url
            if (url != null) {
                task.insert(uriString, url)
            }
        }

        return resolveResult
    }

    private fun canResolveExternally(
        uriString: String,
        darknet: Darknet?,
        isPublicHost: Boolean,
    ): Boolean {
        if (darknet != null) {
            logger.debug("$uriString is a darknet url, but external services are enabled, skipping")
            return false
        }

        if (!isPublicHost) {
            logger.debug("$uriString is not publicly accessible, falling back to local resolving")
            return false
        }

        return true
    }

    private suspend fun <T> resolve(
        localTask: LocalTask<T>,
        resolveType: ResolveType?,
        uriString: String,
        externalService: Boolean,
        darknet: Darknet?,
        isPublicHost: Boolean,
        timeout: Int,
    ): Result<ResolveResultType> {
        if (externalService && canResolveExternally(uriString, darknet, isPublicHost)) {
            logger.debug("Attempting to resolve $uriString via external service..")
            val remoteTask = when (resolveType) {
                ResolveType.Amp2Html -> RemoteTask.Amp2Html
                ResolveType.FollowRedirects -> RemoteTask.Redirector
                else -> null
            }

            if (remoteTask == null) return Result.failure(Exception("No task found"))

            val result = remoteResolver.resolveRemote(
                task = remoteTask,
                url = uriString,
                timeout = timeout,
                remoteResolveUrlField = localTask.remoteResolveUrlField
            )
            if (result.isFailure) {
                logger.error("External resolve failed", result.exceptionOrNull())
            }

            return Result.failure(Exception("No task found"))
        }

        logger.debug("Using local service for $uriString")
        val result = localTask.request.resolveLocal(uriString, timeout)
        if (result.isFailure) logger.error("Local service resolve failed", result.exceptionOrNull())

        return result
    }
}
