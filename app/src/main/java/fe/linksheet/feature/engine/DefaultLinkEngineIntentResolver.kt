package fe.linksheet.feature.engine

import android.content.Context
import app.linksheet.feature.app.core.PackageIntentHandler
import app.linksheet.feature.app.core.PackageLauncherService
import app.linksheet.feature.browser.core.PrivateBrowsingService
import app.linksheet.feature.downloader.Downloader
import app.linksheet.feature.engine.core.EngineScenario
import app.linksheet.feature.engine.core.LinkEngine
import app.linksheet.feature.engine.core.ScenarioSelector
import app.linksheet.feature.engine.core.fetcher.DownloadLinkFetcher
import app.linksheet.feature.engine.core.fetcher.preview.PreviewLinkFetcher
import app.linksheet.feature.engine.core.fetcher.preview.PreviewLocalSource
import app.linksheet.feature.engine.core.modifier.ClearURLsLinkModifier
import app.linksheet.feature.engine.core.modifier.EmbedLinkModifier
import app.linksheet.feature.engine.core.modifier.LibRedirectLinkModifier
import app.linksheet.feature.engine.core.resolver.amp2html.Amp2HtmlLinkResolver
import app.linksheet.feature.engine.core.resolver.amp2html.Amp2HtmlLocalSource
import app.linksheet.feature.engine.core.resolver.followredirects.FollowRedirectsLinkResolver
import app.linksheet.feature.engine.core.resolver.followredirects.FollowRedirectsLocalSource
import app.linksheet.feature.engine.database.repository.CacheRepository
import app.linksheet.feature.libredirect.LibRedirectResolver
import fe.composekit.lifecycle.network.core.NetworkStateService
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.ImprovedBrowserHandler
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.module.IntentResolverSettings
import fe.linksheet.module.resolver.util.AppSorter
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlin.uuid.ExperimentalUuidApi

@Suppress("FunctionName")
@OptIn(ExperimentalUuidApi::class)
fun DefaultLinkEngineIntentResolver(
    context: Context,
    client: HttpClient,
    appSelectionHistoryRepository: AppSelectionHistoryRepository,
    preferredAppRepository: PreferredAppRepository,
    normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    packageIntentHandler: PackageIntentHandler,
    packageLauncherService: PackageLauncherService,
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
//            ExpressionPostProcessorRule(
//                expression = BundleSerializer.decodeFromHexString(
//                    """
//080112b0010a02696612a9010a3f0a055f722e6d6512360a210a025f72121b0a190a016312140a1268747470733a2f2f745c2e6d652f282e2b2912110a027573120b0a090a012412040a02727512660a023d6912600a5e0a04702d3e6912560a210a0163121c0a1a616e64726f69642e696e74656e742e616374696f6e2e5649455712120a035f6175120b0a090a012412040a0272751a1d0a016312180a166f72672e74656c656772616d2e6d657373656e676572
//                        |""".trimMargin().replace("\n", "")
//                ).expression
//            )
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
        position = 0,
        predicate = { true },
        engine = pipeline
    )
    val selector = ScenarioSelector(scenarioFlow = flowOf(listOf(scenario)), dispatcher = dispatcher)

    return LinkEngineIntentResolver(
        context = context,
        client = client,
        appSelectionHistoryRepository = appSelectionHistoryRepository,
        preferredAppRepository = preferredAppRepository,
        normalBrowsersRepository = normalBrowsersRepository,
        inAppBrowsersRepository = inAppBrowsersRepository,
        packageIntentHandler = packageIntentHandler,
        packageLauncherService = packageLauncherService,
        appSorter = appSorter,
        browserHandler = browserHandler,
        inAppBrowserHandler = inAppBrowserHandler,
        networkStateService = networkStateService,
        selector = selector,
        privateBrowsingService = privateBrowsingService,
        settings = settings
    )
}
