package fe.linksheet.intent.engine

import android.content.Context
import app.linksheet.feature.browser.PrivateBrowsingService
import fe.composekit.lifecycle.network.core.NetworkStateService
import fe.linksheet.experiment.engine.EngineScenario
import fe.linksheet.experiment.engine.LinkEngine
import fe.linksheet.experiment.engine.ScenarioSelector
import fe.linksheet.experiment.engine.fetcher.DownloadLinkFetcher
import fe.linksheet.experiment.engine.fetcher.preview.PreviewLinkFetcher
import fe.linksheet.experiment.engine.fetcher.preview.PreviewLocalSource
import fe.linksheet.experiment.engine.modifier.ClearURLsLinkModifier
import fe.linksheet.experiment.engine.modifier.EmbedLinkModifier
import fe.linksheet.experiment.engine.modifier.LibRedirectLinkModifier
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLinkResolver
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLocalSource
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLinkResolver
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLocalSource
import fe.linksheet.feature.app.PackageService
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.Logger
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.ImprovedBrowserHandler
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.module.IntentResolverSettings
import fe.linksheet.module.resolver.util.AppSorter
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("FunctionName")
@OptIn(ExperimentalUuidApi::class)
fun DefaultLinkEngineIntentResolver(
    context: Context,
    logger: Logger,
    client: HttpClient,
    appSelectionHistoryRepository: AppSelectionHistoryRepository,
    preferredAppRepository: PreferredAppRepository,
    normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    packageService: PackageService,
    appSorter: AppSorter,
    downloader: Downloader,
    browserHandler: ImprovedBrowserHandler,
    inAppBrowserHandler: InAppBrowserHandler,
    libRedirectResolver: LibRedirectResolver,
    cacheRepository: CacheRepository,
    networkStateService: NetworkStateService,
    privateBrowsingService: PrivateBrowsingService,
    settings: IntentResolverSettings,
): IntentResolver {
    val dispatcher = Dispatchers.IO
    val pipeline = LinkEngine(
        steps = listOf(
            EmbedLinkModifier(
                enabled = settings.resolveEmbeds,
                ioDispatcher = dispatcher
            ),
            LibRedirectLinkModifier(
                enabled = settings.libRedirectSettings.enableLibRedirect,
                ioDispatcher = dispatcher,
                resolver = libRedirectResolver,
                useJsEngine = settings.libRedirectSettings.libRedirectJsEngine
            ),
            ClearURLsLinkModifier(
                enabled = settings.useClearUrls,
                ioDispatcher = dispatcher
            ),
            FollowRedirectsLinkResolver(
                enabled = settings.followRedirectsSettings.followRedirects,
                ioDispatcher = dispatcher,
                source = FollowRedirectsLocalSource(client = client),
                cacheRepository = cacheRepository,
                allowDarknets = settings.followRedirectsSettings.followRedirectsAllowDarknets,
                allowNonPublic = settings.followRedirectsSettings.followRedirectsAllowLocalNetwork,
                followOnlyKnownTrackers = settings.followRedirectsSettings.followOnlyKnownTrackers,
                useLocalCache = settings.followRedirectsSettings.followRedirectsLocalCache
            ),
            Amp2HtmlLinkResolver(
                enabled = settings.amp2HtmlSettings.enableAmp2Html,
                ioDispatcher = dispatcher,
                source = Amp2HtmlLocalSource(client = client),
                cacheRepository = cacheRepository,
                allowDarknets = settings.amp2HtmlSettings.amp2HtmlAllowDarknets,
                allowNonPublic = settings.amp2HtmlSettings.amp2HtmlAllowLocalNetwork,
                useLocalCache = settings.amp2HtmlSettings.amp2HtmlLocalCache
            )
        ),
//        rules = listOf(
//            IntentPostprocessorRule(
//                matcher = RegexUrlMatcher("""https://t\.me/(.+)""".toRegex()),
//                definition = IntentRuleDefinition(
//                    packageName = "org.telegram.messenger",
//                    cls = null,
//                    action = Intent.ACTION_VIEW,
//                )
//            )
//        ),
        fetchers = listOf(
            DownloadLinkFetcher(
                enabled = settings.downloaderSettings.enableDownloader,
                ioDispatcher = dispatcher,
                downloader = downloader,
                checkUrlMimeType = settings.downloaderSettings.downloaderCheckUrlMimeType,
                requestTimeout = settings.requestTimeout,
            ),
            PreviewLinkFetcher(
                enabled = settings.previewSettings.previewUrl,
                ioDispatcher = dispatcher,
                source = PreviewLocalSource(client = client),
                cacheRepository = cacheRepository,
                useLocalCache = settings.previewSettings.useLocalCache
            )
        ),
        dispatcher = dispatcher
    )

    val scenario = EngineScenario(
        id = Uuid.NIL,
        position = 0,
        predicate = { true },
        engine = pipeline
    )
    val selector = ScenarioSelector(scenarios = listOf(scenario))

    return LinkEngineIntentResolver(
        context = context,
        logger = logger,
        client = client,
        appSelectionHistoryRepository = appSelectionHistoryRepository,
        preferredAppRepository = preferredAppRepository,
        normalBrowsersRepository = normalBrowsersRepository,
        inAppBrowsersRepository = inAppBrowsersRepository,
        packageService = packageService,
        appSorter = appSorter,
        browserHandler = browserHandler,
        inAppBrowserHandler = inAppBrowserHandler,
        networkStateService = networkStateService,
        selector = selector,
        privateBrowsingService = privateBrowsingService,
        settings = settings
    )
}
