package fe.linksheet.module.resolver.urlresolver.base

import android.net.Uri
import fe.gson.extension.json.`object`.asStringOrNull
import fe.httpkt.ext.readToString
import fe.httpkt.isHttpSuccess
import fe.httpkt.json.readToJson
import fe.linksheet.extension.failure
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.extension.unwrapOrNull
import fe.linksheet.module.database.entity.resolver.ResolverEntity
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.repository.resolver.ResolverRepository
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.util.Darknet
import org.koin.core.component.KoinComponent
import java.io.IOException
import kotlin.reflect.KClass


typealias ResolvePredicate = (Uri) -> Boolean

abstract class UrlResolver<T : ResolverEntity<T>, R : Any>(
    clazz: KClass<R>,
    private val redirectResolver: ResolveRequest,
    private val resolverRepository: ResolverRepository<T>,
) : KoinComponent {
    private val logger by injectLogger(clazz)

    suspend fun resolve(
        uri: Uri,
        localCache: Boolean,
        builtInCache: Boolean,
        resolvePredicate: ResolvePredicate? = null,
        externalService: Boolean,
        connectTimeout: Int,
        canAccessInternet: Boolean,
        allowDarknets: Boolean,
    ): Result<ResolveResultType>? {
        val darknet = Darknet.getOrNull(uri)

        if (!allowDarknets && darknet != null) {
            logger.info(uri, HashProcessor.UriProcessor,
                { "$it is a darknet url, but darknets are not allowed, skipping" }
            )
            return null
        }

        val uriString = uri.toString()
        if (localCache) {
            val resolvedUrl = resolverRepository.getForInputUrl(uriString)
            if (resolvedUrl != null) {
                logger.error(resolvedUrl.urlResolved(), HashProcessor.UrlProcessor, { "From local cache: $it" })
                return ResolveResultType.Resolved.LocalCache(resolvedUrl.urlResolved()).wrap()
            }
        }

        if (builtInCache) {
            val resolvedUrl = resolverRepository.getBuiltInCachedForUrl(uriString)
            if (resolvedUrl != null) {
                logger.error(resolvedUrl, HashProcessor.UrlProcessor, { "From built-in cache: $it" })
                return ResolveResultType.Resolved.BuiltInCache(resolvedUrl).wrap()
            }
        }

        if (!canAccessInternet) {
            return ResolveResultType.NoInternetConnection.wrap()
        }

        val resolveResult = resolve(uri, resolvePredicate, externalService, darknet, connectTimeout)

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
        darknet: Darknet?,
        timeout: Int,
    ): Result<ResolveResultType>? {
        logger.error(uri, HashProcessor.UriProcessor, { "Following redirects for $it" })

        val inputUri = uri.toString()
        if (resolvePredicate == null || resolvePredicate(uri)) {
            if (externalService) {
                if (darknet != null) {
                    logger.info(uri, HashProcessor.UriProcessor,
                        { "$it is a darknet url, but external services are enabled, skipping" }
                    )
                    return null
                }

                logger.info(
                    uri, HashProcessor.UriProcessor,
                    { "Using external service for $it" }
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

            logger.error(inputUri, HashProcessor.StringProcessor, { "Using local service for $it" })

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
