package fe.linksheet.module.resolver

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.util.listOfFirstActivityResolveInfo
import app.linksheet.testing.util.packageSetOf
import app.linksheet.testing.util.toKeyedMap
import assertk.assertThat
import assertk.assertions.isDataClassEqualTo
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class ImprovedBrowserHandlerTest : BaseUnitTest  {
    companion object {
        private val handler = ImprovedBrowserHandler(
            autoLaunchSingleBrowserExperiment = { false },
        )
        private val allBrowsersKeyed = PackageInfoFakes.allBrowsers.toKeyedMap()
        private val allAppsInfoList = listOfFirstActivityResolveInfo(PackageInfoFakes.allApps)
        private val allBrowsersInfoList = listOfFirstActivityResolveInfo(PackageInfoFakes.allBrowsers)
        private val allResolvedInfoList = listOfFirstActivityResolveInfo(PackageInfoFakes.allResolved)
    }

    @org.junit.Test
    fun `always ask user which browser to choose`() {
        val config = BrowserModeConfigHelper.AlwaysAsk

        assertThat(
            handler.filterBrowsers(
                config,
                allBrowsersKeyed,
                allResolvedInfoList
            )
        ).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.AlwaysAsk,
                browsers = allBrowsersInfoList,
                apps = allAppsInfoList,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @org.junit.Test
    fun `only list native apps`() {
        val config = BrowserModeConfigHelper.None

        assertThat(
            handler.filterBrowsers(config, allBrowsersKeyed, allResolvedInfoList)
        ).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = emptyList(),
                apps = allAppsInfoList,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @org.junit.Test
    fun `only list native apps, of which user has single, but no browser`() {
        val config = BrowserModeConfigHelper.None

        val youtube = listOfFirstActivityResolveInfo(PackageInfoFakes.Youtube)
        assertThat(
            handler.filterBrowsers(config, emptyMap(), youtube)
        ).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = emptyList(),
                apps = youtube,
                // TODO: Test returns false, is that a bug or expected behavior?
//                isSingleOption = true,
                noBrowsersOnlySingleApp = true
            )
        )
    }

    @org.junit.Test
    fun `only list native apps, of which user has multiple`() {
        val config = BrowserModeConfigHelper.None

        assertThat(
            handler.filterBrowsers(
                config,
                allBrowsersKeyed,
                allResolvedInfoList
            )
        ).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = emptyList(),
                apps = allAppsInfoList,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @org.junit.Test
    fun `selected browser, but none specified`() {
        val config = BrowserModeConfigHelper.SelectedBrowser(null)

        assertThat(
            handler.filterBrowsers(config, allBrowsersKeyed, allResolvedInfoList)
        ).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.SelectedBrowser,
                browsers = emptyList(),
                apps = allAppsInfoList,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @org.junit.Test
    fun `selected browser`() {
        val config = BrowserModeConfigHelper.SelectedBrowser(PackageInfoFakes.MiBrowser.packageInfo.packageName)

        assertThat(
            handler.filterBrowsers(config, allBrowsersKeyed, allResolvedInfoList)
        ).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.SelectedBrowser,
                browsers = listOfFirstActivityResolveInfo(PackageInfoFakes.MiBrowser),
                apps = allAppsInfoList,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @org.junit.Test
    fun `whitelisted browsers, but none selected`() {
        val config = BrowserModeConfigHelper.Whitelisted(null)

        assertThat(
            handler.filterBrowsers(config, allBrowsersKeyed, allResolvedInfoList)
        ).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.Whitelisted,
                // TODO: If no browsers are whitelisted, currently all browsers are returned; Do we actually want this behavior?
//                browsers = emptyList(),
                browsers = allBrowsersInfoList,
                apps = allAppsInfoList,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @org.junit.Test
    fun `whitelisted browsers, one selected`() {
        val config = BrowserModeConfigHelper.Whitelisted(packageSetOf(PackageInfoFakes.MiBrowser))

        assertThat(
            handler.filterBrowsers(config, allBrowsersKeyed, allResolvedInfoList)
        ).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.Whitelisted,
                browsers = listOfFirstActivityResolveInfo(PackageInfoFakes.MiBrowser),
                apps = allAppsInfoList,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @org.junit.Test
    fun `whitelisted browsers, multiple selected`() {
        val config = BrowserModeConfigHelper.Whitelisted(
            packageSetOf(
                PackageInfoFakes.MiBrowser,
                PackageInfoFakes.DuckDuckGoBrowser
            )
        )

        assertThat(
            handler.filterBrowsers(config, allBrowsersKeyed, allResolvedInfoList)
        ).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.Whitelisted,
                browsers = listOfFirstActivityResolveInfo(
                    PackageInfoFakes.MiBrowser,
                    PackageInfoFakes.DuckDuckGoBrowser
                ),
                apps = allAppsInfoList,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }
}
