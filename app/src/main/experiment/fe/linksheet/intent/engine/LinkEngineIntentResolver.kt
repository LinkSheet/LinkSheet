package fe.linksheet.intent.engine

import android.content.Context
import android.net.Uri
import fe.linksheet.experiment.engine.LinkEngine
import fe.linksheet.experiment.engine.context.DefaultEngineRunContext
import fe.linksheet.experiment.engine.context.SourceAppExtra
import fe.linksheet.experiment.engine.context.toSourceAppExtra
import fe.linksheet.extension.std.toStdUrl
import fe.linksheet.module.app.PackageService
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.ImprovedBrowserHandler
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.IntentResolveResult
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.ResolveEvent
import fe.linksheet.module.resolver.ResolverInteraction
import fe.linksheet.module.resolver.module.IntentResolverSettings
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.module.resolver.util.AppSorter
import fe.linksheet.module.resolver.util.ReferrerHelper
import fe.linksheet.util.intent.parser.IntentParser
import fe.linksheet.util.intent.parser.UriException
import fe.linksheet.util.intent.parser.UriParseException
import fe.std.result.isFailure
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mozilla.components.support.utils.SafeIntent

class LinkEngineIntentResolver(
    val context: Context,
    val logger: Logger,
    val linkEngine: LinkEngine,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    private val packageInfoService: PackageService,
    private val appSorter: AppSorter,
    private val downloader: Downloader,
    private val redirectUrlResolver: RedirectUrlResolver,
    private val amp2HtmlResolver: Amp2HtmlUrlResolver,
    private val browserHandler: ImprovedBrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
    private val libRedirectResolver: LibRedirectResolver,
    private val cacheRepository: CacheRepository,
    private val networkStateService: NetworkStateService,
    private val settings: IntentResolverSettings,
) : IntentResolver {
    private val browserSettings = settings.browserSettings
    private val previewSettings = settings.previewSettings
    private val downloaderSettings = settings.downloaderSettings
    private val libRedirectSettings = settings.libRedirectSettings
    private val amp2HtmlSettings = settings.amp2HtmlSettings
    private val followRedirectsSettings = settings.followRedirectsSettings

    private val _events = MutableStateFlow(value = ResolveEvent.Idle)
    override val events = _events.asStateFlow()

    private val _interactions = MutableStateFlow<ResolverInteraction>(value = ResolverInteraction.Idle)
    override val interactions = _interactions.asStateFlow()

    private fun emitEvent(event: ResolveEvent) {
        _events.tryEmit(event)
        logger.info(event.toString())
    }

    private fun emitEventIf(predicate: Boolean, event: ResolveEvent) {
        if (!predicate) return
        emitEvent(event)
    }

    private fun emitInteraction(interaction: ResolverInteraction) {
        _interactions.tryEmit(interaction)
        logger.info("Emitted interaction $interaction")
    }

    private fun clearInteraction() = emitInteraction(ResolverInteraction.Clear)

    private fun fail(error: String, result: IntentResolveResult): IntentResolveResult {
        logger.error(error)
        return result
    }

    private suspend fun initState(event: ResolveEvent, interaction: ResolverInteraction) {
        _events.emit(event)
        _interactions.emit(interaction)
    }

    override suspend fun resolve(
        intent: SafeIntent,
        referrer: Uri?
    ): IntentResolveResult = coroutineScope scope@{
        val canAccessInternet = networkStateService.isNetworkConnected
        val uriResult = IntentParser.getUriFromIntent(intent)
        if (uriResult.isFailure()) {
            logger.error("Failed to parse intent ${intent.action}")
            return@scope IntentResolveResult.IntentParseFailed(uriResult.exception as UriException)
        }

        val uri = uriResult.value.toStdUrl()
        if(uri == null) {
            return@scope IntentResolveResult.IntentParseFailed(UriParseException)
        }

        val referringPackage = ReferrerHelper.getReferringPackage(referrer)
//        emitEvent(ResolveEvent.QueryingBrowsers)
        val browsers = packageInfoService.findHttpBrowsable(null)

        val browserPackageMap = browsers?.associateBy { it.activityInfo.packageName } ?: emptyMap()

//        emitEventIf(shouldRunUriModifiers, ResolveEvent.ApplyingLinkModifiers)

        val sourceAppExtra = referringPackage?.toSourceAppExtra()
        val context= DefaultEngineRunContext(sourceAppExtra)
        val result = linkEngine.process(uri, context)


        TODO("Not yet implemented")
    }

    override suspend fun warmup() {
    }
}
