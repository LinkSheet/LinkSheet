package fe.linksheet.intent.engine

import android.content.Context
import fe.linksheet.experiment.engine.DefaultLinkEngine
import fe.linksheet.experiment.engine.EngineTrack
import fe.linksheet.experiment.engine.TrackSelector
import fe.linksheet.module.app.PackageService
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.Logger
import fe.linksheet.module.network.NetworkStateService
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.ImprovedBrowserHandler
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.module.IntentResolverSettings
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import fe.linksheet.module.resolver.util.AppSorter
import io.ktor.client.HttpClient
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
    libRedirectDefaultRepository: LibRedirectDefaultRepository,
    libRedirectStateRepository: LibRedirectStateRepository,
    packageInfoService: PackageService,
    appSorter: AppSorter,
    downloader: Downloader,
    redirectUrlResolver: RedirectUrlResolver,
    amp2HtmlResolver: Amp2HtmlUrlResolver,
    browserHandler: ImprovedBrowserHandler,
    inAppBrowserHandler: InAppBrowserHandler,
    libRedirectResolver: LibRedirectResolver,
    cacheRepository: CacheRepository,
    networkStateService: NetworkStateService,
    settings: IntentResolverSettings,
): LinkEngineIntentResolver {
    val defaultEngine = DefaultLinkEngine(
        client = client,
        libRedirectResolver = LibRedirectResolver(
            defaultRepository = libRedirectDefaultRepository,
            stateRepository = libRedirectStateRepository
        ),
        cacheRepository = cacheRepository
    )

    val track = EngineTrack(
        id = Uuid.NIL,
        position = 0,
        predicate = { true },
        engine = defaultEngine
    )
    val trackSelector = TrackSelector(tracks = listOf(track))

    return LinkEngineIntentResolver(
        context = context,
        logger = logger,
        client = client,
        appSelectionHistoryRepository = appSelectionHistoryRepository,
        preferredAppRepository = preferredAppRepository,
        normalBrowsersRepository = normalBrowsersRepository,
        inAppBrowsersRepository = inAppBrowsersRepository,
        packageInfoService = packageInfoService,
        appSorter = appSorter,
        downloader = downloader,
        redirectUrlResolver = redirectUrlResolver,
        amp2HtmlResolver = amp2HtmlResolver,
        browserHandler = browserHandler,
        inAppBrowserHandler = inAppBrowserHandler,
        libRedirectResolver = libRedirectResolver,
        cacheRepository = cacheRepository,
        networkStateService = networkStateService,
        trackSelector = trackSelector,
        settings = settings
    )
}
