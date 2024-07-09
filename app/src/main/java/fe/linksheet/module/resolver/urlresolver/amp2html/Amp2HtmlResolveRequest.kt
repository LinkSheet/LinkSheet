package fe.linksheet.module.resolver.urlresolver.amp2html

import android.util.Log
import fe.amp2htmlkt.Amp2Html
import fe.httpkt.Request
import fe.httpkt.ext.getGZIPOrDefaultStream
import fe.httpkt.ext.isHttpSuccess
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.extension.java.normalizedContentType
import fe.linksheet.extension.koin.single
import fe.linksheet.extension.kotlin.failure
import fe.linksheet.extension.okhttp.isHtml
import fe.linksheet.module.resolver.urlresolver.CachedRequest
import fe.linksheet.module.resolver.urlresolver.CachedResponseImpl
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequest
import fe.linksheet.module.resolver.urlresolver.base.ResolveRequestException
import fe.linksheet.util.MimeType
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.io.IOException
import java.io.InputStream
import java.net.URL

val amp2HtmlResolveRequestModule = module {
    single<Amp2HtmlResolveRequest, Request, CachedRequest> { _, request, cachedRequest ->
        Amp2HtmlResolveRequest(
            "${LinkSheetAppConfig.supabaseHost()}/amp2html",
            LinkSheetAppConfig.supabaseApiKey(),
            request,
            cachedRequest,
            scope.get<OkHttpClient>()
        )
    }
}

class Amp2HtmlResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    private val urlResolverCache: CachedRequest,
    private val okHttpClient: OkHttpClient,
) : ResolveRequest(apiUrl, token, request, "amp2html") {
    override fun resolveLocal(url: String, timeout: Int): Result<ResolveResultType> {
        val req = okhttp3.Request.Builder().url(url).build()

        val response = okHttpClient.newCall(req).execute()
        val statusCode = response.code
        val contentType = response.body.contentType() ?: return Result.failure(ResolveRequestException(statusCode))

        if (!response.isSuccessful) {
            return Result.failure(ResolveRequestException(statusCode))
        }

        Log.d("Mime", "$contentType ${contentType.isHtml()}")
        if (!contentType.isHtml()) {
            return Result.success(ResolveResultType.NothingToResolve)
        }

        val nonAmpLink = response.body.byteStream().buffered().use { tryFromCache2(it, req.url.host) }
        if (nonAmpLink != null) {
            Log.d("Amp2Html", "$nonAmpLink")
//            Result.success(ResolveResultType.Resolved.Local(nonAmpLink))

            return nonAmpLink
        }

//        val (current, cache, invalidate, send) = urlResolverCache.getNew(url, timeout, false)
//
//        val cachedNonAmpLink = tryFromCache(current, url)
//        if (cachedNonAmpLink != null) return cachedNonAmpLink
//
//        invalidate()
//
//        val result = try {
//            send()
//        } catch (e: IOException) {
//            return Result.failure(e)
//        }
//
//        val success = result.isHttpSuccess()
//        val contentType = result.normalizedContentType()
//
//        val newCacheItem = CachedResponseImpl(
//            success,
//            result.responseCode,
//            contentType,
//            if (success && contentType == MimeType.TEXT_HTML) result.getGZIPOrDefaultStream()
//                .use { it.readBytes() } else null
//        )
//
//        val newResult = tryFromCache(newCacheItem, url)
//        if (newResult != null) {
//            cache(newCacheItem)
//            return newResult
//        }
//
//        if (!result.isHttpSuccess()) {
//            return Result.failure(ResolveRequestException(result.responseCode))
//        }
//
//        if (!result.isHtml()) {
//            return Result.success(ResolveResultType.NothingToResolve)
//        }
//
//        val nonAmpLink = result.getGZIPOrDefaultStream().use { Amp2Html.getNonAmpLink(it, URL(url).host) }
//        if (nonAmpLink != null) {
//            return Result.success(ResolveResultType.Resolved.Local(nonAmpLink))
//        }
        return Result.failure(ResolveRequestException())
    }

    private fun tryFromCache2(stream: InputStream?, host: String): Result<ResolveResultType>? {
        if (stream == null) return null

//        if (!current.isSuccess) return Result.failure(ResolveRequestException(current.responseCode))
//        if (current.contentType != MimeType.TEXT_HTML) return Result.success(ResolveResultType.NothingToResolve)
//        val body = current.content?.let { String(it) } ?: return null

        val nonAmpLink = Amp2Html.getNonAmpLink(stream, host)
        if (nonAmpLink != null) {
            return Result.success(ResolveResultType.Resolved.Local(nonAmpLink))
        }

        return Result.success(ResolveResultType.NothingToResolve)
    }

    private fun tryFromCache(current: CachedResponseImpl?, url: String): Result<ResolveResultType>? {
        if (current == null) return null

        if (!current.isSuccess) return Result.failure(ResolveRequestException(current.responseCode))
        if (current.contentType != MimeType.TEXT_HTML) return Result.success(ResolveResultType.NothingToResolve)
        val body = current.content?.let { String(it) } ?: return null

        val nonAmpLink = Amp2Html.getNonAmpLink(body, URL(url).host)
        if (nonAmpLink != null) {
            return Result.success(ResolveResultType.Resolved.Local(nonAmpLink))
        }

        return Result.success(ResolveResultType.NothingToResolve)
    }
}
