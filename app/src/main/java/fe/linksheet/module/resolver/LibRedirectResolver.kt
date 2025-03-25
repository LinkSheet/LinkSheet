package fe.linksheet.module.resolver

import LibRedirectResource
import android.net.Uri
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectLoader
import fe.libredirectkt.LibRedirectNew
import fe.libredirectkt.LibRedirectService
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

sealed interface LibRedirectResult {
    class Redirected(val originalUri: Uri, val redirectedUri: Uri) : LibRedirectResult
    data object NotRedirected : LibRedirectResult
}

typealias FrontendInstanceInfo = Pair<String, String>

class LibRedirectResolver(
    private val defaultRepository: LibRedirectDefaultRepository,
    private val stateRepository: LibRedirectStateRepository,
) : KoinComponent {
    private val logger by injectLogger<LibRedirectResolver>()

    companion object {
        private val libRedirectServices by lazy { LibRedirectLoader.loadBuiltInServices() }
        private val libRedirectInstances by lazy { LibRedirectLoader.loadBuiltInInstances() }

        private val libRedirectZipline by lazy {
            LibRedirectResource.getLibRedirect().use { it.readBytes() }
        }
    }

    suspend fun warmup(): Unit = withContext(Dispatchers.IO) {
        libRedirectServices
        libRedirectInstances
        libRedirectZipline
    }

    suspend fun resolve(uri: Uri, jsEngine: Boolean): LibRedirectResult {
        return resolve(uri.toString(), jsEngine)
    }

    suspend fun resolve(url: String, jsEngine: Boolean): LibRedirectResult {
        val service = LibRedirect.findServiceForUrl(url, libRedirectServices)
        logger.debug("Using service: $service")

        if (service == null || !stateRepository.isEnabled(service.key)) {
            return LibRedirectResult.NotRedirected
        }

        val savedDefault = defaultRepository.getByServiceKey(service.key).firstOrNull()
        val info = getFrontendAndInstance(service, savedDefault)
        val redirectedUrl = redirect(url, info, jsEngine)
        logger.debug(redirectedUrl, HashProcessor.StringProcessor) { "Redirected to: $it" }

        if (redirectedUrl == null) {
            return LibRedirectResult.NotRedirected
        }

        return LibRedirectResult.Redirected(Uri.parse(url), Uri.parse(redirectedUrl))
    }

    private fun getFrontendAndInstance(
        service: LibRedirectService,
        savedDefault: LibRedirectDefault?
    ): FrontendInstanceInfo {
        return when {
            savedDefault != null -> savedDefault.frontendKey to getInstanceUrl(savedDefault)
            else -> {
                val frontendKey = service.defaultFrontend.key
                val instanceUrl = LibRedirect.getDefaultInstanceForFrontend(frontendKey, libRedirectInstances)!!
                frontendKey to instanceUrl
            }
        }
    }

    private fun redirect(url: String, info: FrontendInstanceInfo, jsEngine: Boolean): String? {
        val (frontendKey, instanceUrl) = info
        return when {
            jsEngine -> redirectZipline(url, frontendKey, instanceUrl)
            else -> LibRedirect.redirect(url, frontendKey, instanceUrl)
        }
    }

    private fun redirectZipline(url: String, frontendKey: String, instance: String): String? {
        return LibRedirectNew.create(Dispatchers.IO, libRedirectZipline).use {
            it.redirect(url, frontendKey, instance)
        }
    }

    private fun getInstanceUrl(default: LibRedirectDefault): String {
        return when (default.instanceUrl) {
            LibRedirectDefault.randomInstance -> {
                val instance = libRedirectInstances.find { it.frontendKey == default.frontendKey }
                instance?.hosts?.random() ?: default.instanceUrl
            }
            else -> default.instanceUrl
        }
    }
}
