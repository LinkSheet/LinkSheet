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
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class DatabaseExportImportTest : BaseUnitTest {
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
