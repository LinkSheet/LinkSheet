package fe.linksheet.module.resolver

import android.net.Uri
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import kotlinx.coroutines.flow.firstOrNull

class LibRedirectResolver(
    loggerFactory: LoggerFactory,
    private val defaultRepository: LibRedirectDefaultRepository,
    private val stateRepository: LibRedirectStateRepository,
) {
    private val logger = loggerFactory.createLogger(LibRedirectResolver::class)

    private val libRedirectServices by lazy { LibRedirectLoader.loadBuiltInServices() }
    private val libRedirectInstances by lazy { LibRedirectLoader.loadBuiltInInstances() }

    suspend fun resolve(uri: Uri): LibRedirectResult {
        val service = LibRedirect.findServiceForUrl(uri.toString(), libRedirectServices)
        logger.debug("Service=$service")

        if (service != null && stateRepository.isEnabled(service.key)) {
            val savedDefault = defaultRepository.getByServiceKeyFlow(service.key).firstOrNull()
            val (frontendKey, instanceUrl) = if (savedDefault != null) {
                savedDefault.frontendKey to getInstanceUrl(savedDefault)
            } else {
                service.defaultFrontend.key to LibRedirect.getDefaultInstanceForFrontend(
                    service.defaultFrontend.key, libRedirectInstances
                )!!
            }

            val redirected = LibRedirect.redirect(uri.toString(), frontendKey, instanceUrl)
            logger.debug({ "Redirected=$it" }, redirected, HashProcessor.StringProcessor)

            if (redirected != null) {
                return LibRedirectResult.Redirected(uri, Uri.parse(redirected))
            }
        }

        return LibRedirectResult.NotRedirected
    }

    sealed interface LibRedirectResult {
        class Redirected(val originalUri: Uri, val redirectedUri: Uri) : LibRedirectResult
        data object NotRedirected : LibRedirectResult
    }

    private fun getInstanceUrl(default: LibRedirectDefault): String {
        return if (default.instanceUrl == LibRedirectDefault.libRedirectRandomInstanceKey) {
            libRedirectInstances.find { it.frontendKey == default.frontendKey }
                ?.hosts?.random() ?: default.instanceUrl
        } else default.instanceUrl
    }
}