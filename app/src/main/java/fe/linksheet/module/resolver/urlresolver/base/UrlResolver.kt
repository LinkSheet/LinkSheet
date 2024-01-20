package fe.linksheet.module.resolver.urlresolver.base

import android.net.Uri
import fe.gson.extension.json.`object`.asStringOrNull
import fe.httpkt.ext.readToString
import fe.httpkt.isHttpSuccess
import fe.httpkt.json.readToJson
import fe.linksheet.extension.failure
import fe.linksheet.extension.unwrapOrNull
import fe.linksheet.module.database.entity.resolver.ResolverEntity
import fe.linksheet.module.log.hasher.HashProcessor
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.repository.resolver.ResolverRepository
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import java.io.IOException
import kotlin.reflect.KClass


typealias ResolvePredicate = (Uri) -> Boolean

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
        resolvePredicate: ResolvePredicate? = null,
        externalService: Boolean,
        connectTimeout: Int,
        canAccessInternet: Boolean
    ): Result<ResolveResultType>? {
        val uriString = uri.toString()
        if (localCache) {
            val resolvedUrl = resolverRepository.getForInputUrl(uriString)
            if (resolvedUrl != null) {
                logger.error({ "From local cache=$it" }, resolvedUrl.urlResolved(), HashProcessor.UrlProcessor)
                return ResolveResultType.Resolved.LocalCache(resolvedUrl.urlResolved()).wrap()
            }
        }

        if (builtInCache) {
            val resolvedUrl = resolverRepository.getBuiltInCachedForUrl(uriString)
            if (resolvedUrl != null) {
                logger.error({ "From built-in cache: $it" }, resolvedUrl, HashProcessor.UrlProcessor)
                return ResolveResultType.Resolved.BuiltInCache(resolvedUrl).wrap()
            }
        }

        if (!canAccessInternet) {
            return ResolveResultType.NoInternetConnection.wrap()
        }

        val resolveResult = resolve(uri, resolvePredicate, externalService, connectTimeout)

        if (localCache) {
            val url = resolveResult?.unwrapOrNull<ResolveResultType, ResolveResultType.Resolved>()?.url
            if (url != null) {
                resolverRepository.insert(uriString, url)
            }
        }

        return resolveResult
    }

    private fun resolve(
        uri: Uri,
        resolvePredicate: ResolvePredicate? = null,
        externalService: Boolean,
        timeout: Int,
    ): Result<ResolveResultType>? {
        logger.error({ "Following redirects for $it" }, uri, HashProcessor.UriProcessor)

        val inputUri = uri.toString()
        if (resolvePredicate == null || resolvePredicate(uri)) {
            if (externalService) {
                logger.error(
                    { "Using external service for $it" },
                    inputUri,
                    HashProcessor.StringProcessor
                )

                val con = try {
                    redirectResolver.resolveRemote(inputUri, timeout)
                } catch (e: IOException) {
                    logger.error(e)
                    return Result.failure(e)
                }

                if (!isHttpSuccess(con.responseCode)) {
                    logger.error("Failed to resolve via external service (${con.responseCode}): ${con.readToString()}")
                    return Result.failure("Something went wrong while resolving redirect (${con.responseCode})")
                }

                val obj = con.readToJson().asJsonObject
                val remoteResolveUrlField = resolverRepository.remoteResolveUrlField

                val resolvedUrl = obj.asStringOrNull(remoteResolveUrlField)

                return if (resolvedUrl != null) {
                    ResolveResultType.Resolved.Remote(resolvedUrl).wrap()
                } else {
                    logger.error("Failed to read resolve response (attempted to get '$remoteResolveUrlField' on $obj)")
                    Result.failure("Something went wrong while reading the response (attempted to get '$remoteResolveUrlField' on $obj)")
                }
            }

            logger.error({ "Using local service for $it" }, inputUri, HashProcessor.StringProcessor)

            try {
                val resolved = redirectResolver.resolveLocal(inputUri, timeout)
                if (resolved != null) {
                    return ResolveResultType.Resolved.Local(resolved).wrap()
                }
            } catch (e: IOException) {
                logger.error(e)
                return Result.failure(e)
            }
        }

        return null
    }
}
