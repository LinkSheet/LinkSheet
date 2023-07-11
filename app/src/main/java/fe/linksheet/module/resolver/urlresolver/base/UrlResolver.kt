package fe.linksheet.module.resolver.urlresolver.base

import android.net.Uri
import fe.gson.extensions.string
import fe.httpkt.ext.readToString
import fe.httpkt.isHttpSuccess
import fe.httpkt.json.readToJson
import fe.linksheet.extension.failure
import fe.linksheet.module.database.entity.resolver.ResolverEntity
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.repository.resolver.ResolverRepository
import fe.linksheet.module.resolver.urlresolver.ResolveType
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException
import kotlin.reflect.KClass


abstract class UrlResolver<T : ResolverEntity<T>, R : Any>(
    loggerFactory: LoggerFactory,
    clazz: KClass<R>,
    private val redirectResolver: ResolveRequest,
    private val resolverRepository: ResolverRepository<T>,
) {
    private val logger = loggerFactory.createLogger(clazz)

    suspend fun resolve(
        uri: Uri,
        localCache: Boolean,
        builtInCache: Boolean,
        resolvePredicate: (String) -> Boolean,
        externalService: Boolean,
        connectTimeout: Int
    ): Result<ResolveType> {
        val uriString = uri.toString()
        if (localCache) {
            val resolvedUrl = resolverRepository.getForInputUrl(uriString).firstOrNull()
            if (resolvedUrl != null) {
                logger.debug("From local cache: %s", resolvedUrl)
                return Result.success(ResolveType.LocalCache(resolvedUrl.urlResolved()))
            }
        }

        if (builtInCache) {
            val resolvedUrl = resolverRepository.getBuiltInCachedForUrl(uriString)
            if (resolvedUrl != null) {
                logger.debug("From built-in cache: %s", resolvedUrl)
                return Result.success(ResolveType.BuiltInCache(resolvedUrl))
            }
        }

        val followRedirect = resolve(uri, resolvePredicate, externalService, connectTimeout)

        if (localCache && followRedirect.isSuccess && followRedirect.getOrNull() !is ResolveType.NotResolved) {
            resolverRepository.insert(uriString, followRedirect.getOrNull()?.url!!)
        }

        return followRedirect
    }

    private fun resolve(
        uri: Uri,
        resolvePredicate: (String) -> Boolean,
        externalService: Boolean,
        timeout: Int
    ): Result<ResolveType> {
        logger.debug("Following redirects for %s", uri, HashProcessor.UriProcessor)

        val inputUri = uri.toString()
        if (resolvePredicate(inputUri)) {
            if (externalService) {
                logger.debug(
                    "Using external service for %s",
                    inputUri,
                    HashProcessor.StringProcessor
                )

                val con = try {
                    redirectResolver.resolveRemote(inputUri, timeout)
                } catch (e: IOException) {
                    logger.debug(e)
                    return Result.failure(e)
                }

                if (!isHttpSuccess(con.responseCode)) {
                    logger.debug("Failed to resolve via external service (${con.responseCode}): ${con.readToString()}")
                    return failure("Something went wrong while resolving redirect (response code ${con.responseCode})")
                }

                val obj = con.readToJson().asJsonObject
                val remoteResolveUrlField = resolverRepository.remoteResolveUrlField

                return obj.string(remoteResolveUrlField)?.let {
                    Result.success(ResolveType.Remote(it))
                } ?: failure(
                    "Something went wrong while reading the response (attempted to get '$remoteResolveUrlField' on $obj)"
                )
            }

            logger.debug("Using local service for %s", inputUri, HashProcessor.StringProcessor)

            try {
                val resolved = redirectResolver.resolveLocal(inputUri, timeout)
                if (resolved != null) {
                    return Result.success(ResolveType.Local(resolved))
                }
            } catch (e: IOException) {
                logger.debug(e)
                return Result.failure(e)
            }
        }

        return Result.success(ResolveType.NotResolved(inputUri))
    }
}