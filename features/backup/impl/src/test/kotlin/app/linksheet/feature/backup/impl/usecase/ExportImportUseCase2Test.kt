package app.linksheet.feature.backup.impl.usecase

//import fe.linksheet.module.database.dao.AppSelectionHistoryDao
//import fe.linksheet.module.database.dao.DisableInAppBrowserInSelectedDao
//import fe.linksheet.module.database.dao.PreferredAppDao
//import fe.linksheet.module.database.dao.resolver.Amp2HtmlMappingDao
//import fe.linksheet.module.database.dao.resolver.ResolvedRedirectDao
//import fe.linksheet.module.database.dao.whitelisted.WhitelistedInAppBrowsersDao
//import fe.linksheet.module.database.dao.whitelisted.WhitelistedNormalBrowsersDao
//import fe.linksheet.module.database.entity.AppSelectionHistory
//import fe.linksheet.module.database.entity.DisableInAppBrowserInSelected
//import fe.linksheet.module.database.entity.PreferredApp
//import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
//import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
//import fe.linksheet.module.database.entity.whitelisted.WhitelistedInAppBrowser
//import fe.linksheet.module.database.entity.whitelisted.WhitelistedNormalBrowser
//import fe.linksheet.module.preference.app.DefaultAppPreferenceRepository
//import fe.linksheet.module.preference.experiment.ExperimentRepository
//import fe.linksheet.module.preference.state.DefaultAppStateRepository
//import fe.linksheet.module.repository.AppSelectionHistoryRepository
//import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
//import fe.linksheet.module.repository.PreferredAppRepository
//import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
//import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
//import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
//import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
//import fe.linksheet.testlib.core.BaseUnitTest
//import io.mockk.every
//import io.mockk.mockk
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.BAKLAVA])
internal class DatabaseExportImportTest : BaseUnitTest {
    // TODO: Handle previous exports:
//    {
//        "preferences": [
//        {
//            "name": "always_show_package_name",
//            "value": "false"
//        },
//        {
//            "name": "use_clear_urls",
//            "value": "true"
//        },
//        {
//            "name": "fast_forward_rules",
//            "value": "true"
//        },
//        {
//            "name": "follow_redirects_timeout",
//            "value": "15"
//        },
//        {
//            "name": "show_as_referrer",
//            "value": "true"
//        },
//        {
//            "name": "dev_mode_enabled",
//            "value": "true"
//        },
//        {
//            "name": "first_run",
//            "value": "true"
//        },
//        {
//            "name": "resolve_embeds",
//            "value": "true"
//        },
//        {
//            "name": "last_version",
//            "value": "2026052502"
//        },
//        {
//            "name": "last_versions_v0",
//            "value": "[{\"v\":2025061001,\"f\":\"Pro-nightly\"},{\"v\":2025061202,\"f\":\"Pro-nightly\"},{\"v\":2025070203,\"f\":\"Pro-nightly\"},{\"v\":2025070204,\"f\":\"Pro-nightly\"},{\"v\":2025070205,\"f\":\"Pro-nightly\"},{\"v\":2025070302,\"f\":\"Pro-nightly\"},{\"v\":2025071102,\"f\":\"Pro-nightly\"},{\"v\":2025071402,\"f\":\"Pro-nightly\"},{\"v\":2025072502,\"f\":\"Pro-nightly\"},{\"v\":2025080101,\"f\":\"Pro-nightly\"},{\"v\":2025080301,\"f\":\"Pro-nightly\"},{\"v\":2025082901,\"f\":\"Pro-nightly\"},{\"v\":2025090201,\"f\":\"Pro-nightly\"},{\"v\":2025100502,\"f\":\"Pro-nightly\"},{\"v\":2025101502,\"f\":\"Pro-nightly\"},{\"v\":2025102402,\"f\":\"Pro-nightly\"},{\"v\":2025110201,\"f\":\"Pro-nightly\"},{\"v\":2025112401,\"f\":\"Pro-nightly\"},{\"v\":2025112402,\"f\":\"Pro-nightly\"},{\"v\":2025112403,\"f\":\"Pro-nightly\"},{\"v\":2025112404,\"f\":\"Pro-nightly\"},{\"v\":2025112501,\"f\":\"Pro-nightly\"},{\"v\":2025112701,\"f\":\"Pro-nightly\"},{\"v\":2025112702,\"f\":\"Pro-nightly\"},{\"v\":2026022002,\"f\":\"Pro-nightly\"},{\"v\":2026022003,\"f\":\"Pro-nightly\"},{\"v\":2026022501,\"f\":\"Pro-nightly\"},{\"v\":2026033103,\"f\":\"Pro-nightly\"},{\"v\":2026040304,\"f\":\"Pro-nightly\"},{\"v\":2026042201,\"f\":\"Pro-nightly\"},{\"v\":2026042701,\"f\":\"Pro-nightly\"},{\"v\":2026050602,\"f\":\"Pro-nightly\"},{\"v\":2026050701,\"f\":\"Pro-nightly\"},{\"v\":2026050803,\"f\":\"Pro-nightly\"},{\"v\":2026050901,\"f\":\"Pro-nightly\"},{\"v\":2026052502,\"f\":\"Pro-nightly\"}]"
//        },
//        {
//            "name": "home_clipboard_card",
//            "value": "true"
//        },
//        {
//            "name": "preview_url",
//            "value": "true"
//        },
//        {
//            "name": "auto_launch_single_browser",
//            "value": "false"
//        },
//        {
//            "name": "selected_browser",
//            "value": "org.mozilla.fennec_fdroid/org.mozilla.fenix.IntentReceiverActivity"
//        },
//        {
//            "name": "browser_mode",
//            "value": "AlwaysAsk"
//        },
//        {
//            "name": "in_app_browser_mode",
//            "value": "AlwaysAsk"
//        },
//        {
//            "name": "unified_preferred_browser",
//            "value": "true"
//        },
//        {
//            "name": "in_app_browser_setting",
//            "value": "AlwaysDisableInAppBrowser"
//        },
//        {
//            "name": "hide_after_copying",
//            "value": "false"
//        },
//        {
//            "name": "usage_stats_sorting",
//            "value": "false"
//        },
//        {
//            "name": "grid_layout",
//            "value": "false"
//        },
//        {
//            "name": "dont_show_filtered_item",
//            "value": "false"
//        },
//        {
//            "name": "hide_bottom_sheet_choice_buttons",
//            "value": "false"
//        },
//        {
//            "name": "expand_on_app_select",
//            "value": "true"
//        },
//        {
//            "name": "bottom_sheet_native_label",
//            "value": "true"
//        },
//        {
//            "name": "hide_referrer_from_sheet",
//            "value": "false"
//        },
//        {
//            "name": "double_tap_url",
//            "value": "true"
//        },
//        {
//            "name": "expand_fully",
//            "value": "false"
//        },
//        {
//            "name": "url_bar_preview",
//            "value": "true"
//        },
//        {
//            "name": "url_bar_preview_skip_browser",
//            "value": "true"
//        },
//        {
//            "name": "tap_config_single",
//            "value": "SelectItem"
//        },
//        {
//            "name": "tap_config_double",
//            "value": "OpenApp"
//        },
//        {
//            "name": "tap_config_long",
//            "value": "OpenSettings"
//        },
//        {
//            "name": "url_copied_toast",
//            "value": "true"
//        },
//        {
//            "name": "download_started_toast",
//            "value": "true"
//        },
//        {
//            "name": "opening_with_app_toast",
//            "value": "true"
//        },
//        {
//            "name": "resolve_via_toast",
//            "value": "true"
//        },
//        {
//            "name": "resolve_via_failed_toast",
//            "value": "true"
//        },
//        {
//            "name": "enable_amp2html",
//            "value": "false"
//        },
//        {
//            "name": "amp2html_local_cache",
//            "value": "true"
//        },
//        {
//            "name": "amp2html_external_service",
//            "value": "false"
//        },
//        {
//            "name": "amp2html_allow_darknets",
//            "value": "false"
//        },
//        {
//            "name": "amp2html_allow_local_network",
//            "value": "false"
//        },
//        {
//            "name": "amp2html_skip_browser",
//            "value": "true"
//        },
//        {
//            "name": "enable_downloader",
//            "value": "false"
//        },
//        {
//            "name": "downloader_mode",
//            "value": "Auto"
//        },
//        {
//            "name": "downloaderCheckUrlMimeType",
//            "value": "true"
//        },
//        {
//            "name": "downloader_request_timeout",
//            "value": "15"
//        },
//        {
//            "name": "follow_redirects",
//            "value": "true"
//        },
//        {
//            "name": "follow_redirects_mode",
//            "value": "Auto"
//        },
//        {
//            "name": "follow_redirects_aggressive",
//            "value": "false"
//        },
//        {
//            "name": "follow_redirects_local_cache",
//            "value": "true"
//        },
//        {
//            "name": "follow_redirects_external_service",
//            "value": "false"
//        },
//        {
//            "name": "follow_only_known_trackers",
//            "value": "false"
//        },
//        {
//            "name": "follow_redirects_allow_darknets",
//            "value": "false"
//        },
//        {
//            "name": "follow_redirects_allow_local_network",
//            "value": "false"
//        },
//        {
//            "name": "follow_redirects_skip_browser",
//            "value": "true"
//        },
//        {
//            "name": "theme_v2",
//            "value": "Dark"
//        },
//        {
//            "name": "theme_material_you",
//            "value": "true"
//        },
//        {
//            "name": "theme_amoled_enabled",
//            "value": "false"
//        },
//        {
//            "name": "enable_lib_redirect",
//            "value": "false"
//        },
//        {
//            "name": "enable_ignore_lib_redirect_button",
//            "value": "false"
//        },
//        {
//            "name": "enable_shizuku",
//            "value": "false"
//        },
//        {
//            "name": "auto_disable_link_handling",
//            "value": "false"
//        },
//        {
//            "name": "enable_request_private_browsing_button",
//            "value": "true"
//        },
//        {
//            "name": "bottom_sheet_profile_switcher",
//            "value": "true"
//        },
//        {
//            "name": "send_target",
//            "value": "true"
//        },
//        {
//            "name": "telemetry_dialog",
//            "value": "true"
//        },
//        {
//            "name": "remote_config",
//            "value": "true"
//        },
//        {
//            "name": "theme",
//            "value": "0"
//        }
//        ]
//    }

//    private inline fun <Entity, reified Dao : BaseDao<Entity>> mockDao(entities: List<Entity>): Dao {
//        val dao = mockk<Dao>()
//        every { dao.getAll() }.returns(flowOf(entities))
//        return dao
//    }

//    private val preferenceRepository = DefaultAppPreferenceRepository(applicationContext)
//    private val experimentRepository = ExperimentRepository(applicationContext)
//    private val appStateRepository = DefaultAppStateRepository(applicationContext)

    @Test
    fun test() = runTest {
//        val preferredApps = listOf(
//            PreferredApp(
//                id = 1,
//                host = "www.youtube.com",
//                _packageName = "io.github.forkmaintainers.iceraven",
//                _component = "io.github.forkmaintainers.iceraven/org.mozilla.fenix.IntentReceiverActivity",
//                alwaysPreferred = true
//            ),
//            PreferredApp(
//                id = 2,
//                host = "x.com",
//                _packageName = "com.twitter.android",
//                _component = "com.twitter.android/com.twitter.deeplink.implementation.UrlInterpreterActivity",
//                alwaysPreferred = true
//            )
//        )
//
//        val disableInAppBrowserInSelected = listOf(
//            DisableInAppBrowserInSelected(
//                id = 1,
//                packageName = "com.google.android.ext.shared"
//            ),
//            DisableInAppBrowserInSelected(
//                id = 2,
//                packageName = "com.google.android.apps.restore"
//            ),
//        )
//
//        val whitelistedNormalBrowsers = listOf(
//            WhitelistedNormalBrowser(
//                id = 1,
//                packageName = "com.android.chrome/com.google.android.apps.chrome.IntentDispatcher"
//            ),
//            WhitelistedNormalBrowser(
//                id = 2,
//                packageName = "eu.kanade.tachiyomi.extension.all.anyweb/eu.kanade.tachiyomi.extension.all.anyweb.AnyWebUrlActivity"
//            )
//        )
//
//        val whitelistedInAppBrowsers = listOf(
//            WhitelistedInAppBrowser(
//                id = 1,
//                packageName = "org.mozilla.fennec_fdroid/org.mozilla.fenix.IntentReceiverActivity"
//            ),
//        )
//
//        val appSelectionHistories = listOf(
//            AppSelectionHistory(
//                id = 2,
//                host = "x.com",
//                packageName = "com.twitter.android",
//                lastUsed = 1780139666278
//            )
//        )
//        val resolvedRedirects = listOf(
//            ResolvedRedirect(
//                shortUrl = "https://t.co/12345",
//                resolvedUrl = "https://linksheet.app/12345"
//            )
//        )
//        val amp2HtmlMappings = listOf(
//            Amp2HtmlMapping(
//                ampUrl = "https://amp.com/12345",
//                canonicalUrl = "https://linksheet.app/12345"
//            )
//        )
//
//        val useCase = ExportImportUseCase2(
//            json = Json.Default,
//            preferenceRepository = preferenceRepository,
//            experimentRepository = experimentRepository,
//            appStateRepository = appStateRepository,
//            preferredAppRepository = PreferredAppRepository(
//                dao = mockDao<PreferredApp, PreferredAppDao>(preferredApps)
//            ),
//            disableInAppBrowserInSelectedRepository = DisableInAppBrowserInSelectedRepository(
//                dao = mockDao<DisableInAppBrowserInSelected, DisableInAppBrowserInSelectedDao>(disableInAppBrowserInSelected)
//            ),
//            whitelistedNormalBrowsersRepository = WhitelistedNormalBrowsersRepository(
//                dao = mockDao<WhitelistedNormalBrowser, WhitelistedNormalBrowsersDao>(whitelistedNormalBrowsers)
//            ),
//            whitelistedInAppBrowsersRepository = WhitelistedInAppBrowsersRepository(
//                dao = mockDao<WhitelistedInAppBrowser, WhitelistedInAppBrowsersDao>(whitelistedInAppBrowsers)
//            ),
//            appSelectionHistoryRepository = AppSelectionHistoryRepository(
//                dao = mockDao<AppSelectionHistory, AppSelectionHistoryDao>(appSelectionHistories)
//            ),
//            resolvedRedirectRepository = ResolvedRedirectRepository(
//                dao = mockDao<ResolvedRedirect, ResolvedRedirectDao>(resolvedRedirects)
//            ),
//            amp2HtmlRepository = Amp2HtmlRepository(
//                dao = mockDao<Amp2HtmlMapping, Amp2HtmlMappingDao>(amp2HtmlMappings)
//            )
//        )
//        val str = useCase.exportToString(true)
//        println(str)
//        assertThat(json).isEqualTo(expectedJson)
    }
}
