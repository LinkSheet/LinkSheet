package fe.linksheet.module.resolver

import android.net.Uri
import androidx.annotation.StringRes
import com.google.gson.JsonObject
import fe.fastforwardkt.isTracker
import fe.gson.extensions.string
import fe.httpkt.json.readToJson
import fe.linksheet.R
import fe.linksheet.module.database.entity.ResolvedRedirect
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.log.UrlProcessor
import fe.linksheet.module.repository.ResolvedRedirectRepository
import kotlinx.coroutines.flow.first

class RedirectFollower(
    loggerFactory: LoggerFactory,
    private val redirectResolver: RedirectResolver,
    private val resolvedRedirectRepository: ResolvedRedirectRepository,
) {
    private val logger = loggerFactory.createLogger(RedirectFollower::class)

    sealed class FollowRedirect(@StringRes val stringId: Int, val resolvedUrl: String) {
        class Cache(resolvedUrl: String) : FollowRedirect(
            R.string.redirect_resolve_type_cache, resolvedUrl
        )

        class Remote(resolvedUrl: String) : FollowRedirect(
            R.string.redirect_resolve_type_remote, resolvedUrl
        )

        class Local(resolvedUrl: String) : FollowRedirect(
            R.string.redirect_resolve_type_local, resolvedUrl
        )

        class NotResolved(resolvedUrl: String) : FollowRedirect(
            R.string.redirect_resolve_type_not_resolved, resolvedUrl
        )
    }

    suspend fun followRedirects(
        uri: Uri,
        localCache: Boolean,
        fastForwardRulesObject: JsonObject,
        onlyKnownTrackers: Boolean,
        externalService: Boolean
    ): Result<FollowRedirect> {
        if (localCache) {
            val redirect = resolvedRedirectRepository.getForShortUrl(uri.toString()).first()
            if (redirect != null) {
                logger.debug("From local cache: %s", redirect)

                return Result.success(FollowRedirect.Cache(redirect.resolvedUrl))
            }
        }

        val followRedirect = followRedirectsImpl(
            uri,
            fastForwardRulesObject,
            onlyKnownTrackers,
            externalService
        )

        if (localCache && followRedirect.getOrNull() !is FollowRedirect.NotResolved) {
            resolvedRedirectRepository.insert(
                ResolvedRedirect(uri.toString(), followRedirect.getOrNull()?.resolvedUrl!!)
            )
        }

        return followRedirect
    }

    private fun followRedirectsImpl(
        uri: Uri,
        fastForwardRulesObject: JsonObject,
        onlyKnownTrackers: Boolean,
        externalService: Boolean
    ): Result<FollowRedirect> {
        logger.debug("Following redirects for %s", uri, HashProcessor.UriProcessor)

        val followUri = uri.toString()
        if (!onlyKnownTrackers || isTracker(followUri, fastForwardRulesObject)) {
            if (externalService) {
                logger.debug("Using external service for %s", followUri, HashProcessor.StringProcessor)

                val con = redirectResolver.resolveRemote(followUri)
                if (con.responseCode != 200) {
                    return Result.failure(Exception("Something went wrong while resolving redirect"))
                }

                val obj = con.readToJson().asJsonObject

                return obj.string("resolvedUrl")?.let {
                    Result.success(FollowRedirect.Remote(it))
                } ?: Result.failure(Exception("Something went wrong while reading response"))
            }

            logger.debug("Using local service for %s", followUri, HashProcessor.StringProcessor)
            //TODO: error handling?
            val resolved = redirectResolver.resolveLocal(followUri).url.toString()

            return Result.success(FollowRedirect.Local(resolved))
        }

        return Result.success(FollowRedirect.NotResolved(followUri))
    }
}