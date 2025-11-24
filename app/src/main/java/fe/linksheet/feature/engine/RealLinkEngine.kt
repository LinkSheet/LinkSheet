@file:OptIn(ExperimentalUuidApi::class)

package fe.linksheet.feature.engine

import android.content.Context
import app.linksheet.feature.app.PackageService
import app.linksheet.feature.browser.PrivateBrowsingService
import app.linksheet.feature.downloader.Downloader
import app.linksheet.feature.engine.core.EngineResult
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
import app.linksheet.feature.engine.core.rule.Rule
import app.linksheet.feature.engine.database.entity.ExpressionRule
import app.linksheet.feature.engine.database.entity.ExpressionRuleType
import app.linksheet.feature.engine.database.entity.Scenario
import app.linksheet.feature.engine.database.repository.CacheRepository
import app.linksheet.feature.engine.database.repository.ScenarioRepository
import app.linksheet.feature.engine.eval.rule.ExpressionPostProcessorRule
import app.linksheet.feature.engine.eval.rule.ExpressionPreProcessorRule
import app.linksheet.feature.libredirect.LibRedirectResolver
import fe.composekit.lifecycle.network.core.NetworkStateService
import fe.linksheet.module.log.Logger
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.resolver.ImprovedBrowserHandler
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.module.IntentResolverSettings
import fe.linksheet.module.resolver.util.AppSorter
import io.ktor.client.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi

class RealLinkEngine(
    private val context: Context,
    private val logger: Logger,
    private val client: HttpClient,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    private val inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    private val packageService: PackageService,
    private val appSorter: AppSorter,
    private val downloader: Downloader,
    private val browserHandler: ImprovedBrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
    private val libRedirectResolver: LibRedirectResolver,
    private val cacheRepository: CacheRepository,
    private val networkStateService: NetworkStateService,
    private val privateBrowsingService: PrivateBrowsingService,
    private val scenarioRepository: ScenarioRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun createResolver(settings: IntentResolverSettings): LinkEngineIntentResolver {
        val scenarios = scenarioRepository
            .getAllScenarioExpressions()
            .map { it.ifEmpty { ScenarioRepository.DefaultScenario } }
            .map { it.toEngineScenarios(settings) }

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
            selector = ScenarioSelector(scenarioFlow = scenarios, dispatcher = dispatcher),
            privateBrowsingService = privateBrowsingService,
            settings = settings
        )
    }

    private fun Map<Scenario, List<ExpressionRule>>.toEngineScenarios(settings: IntentResolverSettings): List<EngineScenario> {
        return map { (scenario, expressions) ->
            val rules = expressions.map { it.toRule() }
            scenario.toEngineScenario(settings, rules)
        }
    }


    private fun ExpressionRule.toRule(): Rule<*, EngineResult> {
        val bundle = scenarioRepository.toBundle(this)
        return when (type) {
            ExpressionRuleType.Pre -> ExpressionPreProcessorRule(bundle.expression)
            ExpressionRuleType.Post -> ExpressionPostProcessorRule(bundle.expression)
        }
    }

    private fun Scenario.toEngineScenario(settings: IntentResolverSettings, rules: List<Rule<*, EngineResult>>): EngineScenario {
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
            rules = rules,
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
            position = position,
            // TODO: Get from stored Scenario entity
            predicate = { true },
            engine = pipeline
        )

        return scenario
    }
}
