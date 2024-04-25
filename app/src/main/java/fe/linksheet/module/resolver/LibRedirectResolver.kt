package fe.linksheet.module.resolver

import android.net.Uri
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectLoader
import fe.libredirectkt.LibRedirectNew
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent

sealed interface LibRedirectResult {
    class Redirected(val originalUri: Uri, val redirectedUri: Uri) : LibRedirectResult
    data object NotRedirected : LibRedirectResult
}

class LibRedirectResolver(
    private val defaultRepository: LibRedirectDefaultRepository,
    private val stateRepository: LibRedirectStateRepository,
) : KoinComponent {
    private val logger by injectLogger<LibRedirectResolver>()

    companion object {
        private val libRedirectServices = LibRedirectLoader.loadBuiltInServices()
        private val libRedirectInstances = LibRedirectLoader.loadBuiltInInstances()

        private val libRedirectZipline by lazy {
            LibRedirectResource.getLibRedirect().use { it.readBytes() }
        }
    }

    suspend fun resolve(uri: Uri, jsEngine: Boolean): LibRedirectResult {
        val service = LibRedirect.findServiceForUrl(uri.toString(), libRedirectServices)
        logger.debug("Using service: $service")

        if (service != null && stateRepository.isEnabled(service.key)) {
            val savedDefault = defaultRepository.getByServiceKey(service.key).firstOrNull()
            val (frontendKey, instanceUrl) = if (savedDefault != null) {
                savedDefault.frontendKey to getInstanceUrl(savedDefault)
            } else {
                service.defaultFrontend.key to LibRedirect.getDefaultInstanceForFrontend(
                    service.defaultFrontend.key, libRedirectInstances
                )!!
            }

            val redirected = if (jsEngine) {
                redirectZipline(uri.toString(), frontendKey, instanceUrl)
            } else LibRedirect.redirect(uri.toString(), frontendKey, instanceUrl)

            logger.debug(redirected, HashProcessor.StringProcessor) { "Redirected to: $it" }

            if (redirected != null) {
                return LibRedirectResult.Redirected(uri, Uri.parse(redirected))
            }
        }

        return LibRedirectResult.NotRedirected
    }

    private fun redirectZipline(url: String, frontendKey: String, instance: String): String? {
        return LibRedirectNew.create(Dispatchers.IO, libRedirectZipline).use {
            it.redirect(url, frontendKey, instance)
        }
    }

    private fun getInstanceUrl(default: LibRedirectDefault): String {
        return if (default.instanceUrl == LibRedirectDefault.randomInstance) {
            libRedirectInstances.find { it.frontendKey == default.frontendKey }
                ?.hosts?.random() ?: default.instanceUrl
        } else default.instanceUrl
    }

}
