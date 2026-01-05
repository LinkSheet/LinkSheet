package fe.linksheet.module.resolver

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.util.firstActivityResolveInfo
import app.linksheet.testing.util.listOfFirstActivityResolveInfo
import app.linksheet.testing.util.packageSetOf
import assertk.assertThat
import assertk.assertions.isDataClassEqualTo
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class ImprovedBrowserHandlerTest : BaseUnitTest  {
    companion object {
        private val handler = ImprovedBrowserHandler()
        internal val allBrowsersResolveInfos = PackageInfoFakes.allBrowsers.mapNotNull { it.firstActivityResolveInfo }
        private val allAppsInfoList = listOfFirstActivityResolveInfo(PackageInfoFakes.allApps)
        private val allBrowsersInfoList = listOfFirstActivityResolveInfo(PackageInfoFakes.allBrowsers)
        private val allResolvedInfoList = listOfFirstActivityResolveInfo(PackageInfoFakes.allResolved)
    }

    @org.junit.Test
    fun `always ask user which browser to choose`() {
        val config = BrowserModeConfigHelper.AlwaysAsk

        val result = handler.filterBrowsers(config, false, allBrowsersResolveInfos, allResolvedInfoList)
        assertThat(result).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = config.mode,
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

        val result = handler.filterBrowsers(config, false, allBrowsersResolveInfos, allResolvedInfoList)
        assertThat(result).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = config.mode,
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
        val result = handler.filterBrowsers(config, false, emptyList(), youtube)
        assertThat(result).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = config.mode,
                browsers = emptyList(),
                apps = youtube,
                isSingleOption = true,
                noBrowsersOnlySingleApp = true
            )
        )
    }

    @org.junit.Test
    fun `only list native apps, of which user has multiple`() {
        val config = BrowserModeConfigHelper.None

        val result = handler.filterBrowsers(config, false, allBrowsersResolveInfos, allResolvedInfoList)
        assertThat(result).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = config.mode,
                browsers = emptyList(),
                apps = allAppsInfoList,
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @org.junit.Test
    fun `only list native apps, of which user has none`() {
        val config = BrowserModeConfigHelper.None

        val result = handler.filterBrowsers(config, false, allBrowsersResolveInfos, emptyList())
        assertThat(result).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = config.mode,
                browsers = allBrowsersResolveInfos,
                apps = emptyList(),
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @org.junit.Test
    fun `selected browser, but none specified`() {
        val config = BrowserModeConfigHelper.SelectedBrowser(null)

        val result = handler.filterBrowsers(config, false, allBrowsersResolveInfos, allResolvedInfoList)
        assertThat(result).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = config.mode,
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

        val result = handler.filterBrowsers(config, false,allBrowsersResolveInfos, allResolvedInfoList)
        assertThat(result).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = config.mode,
                browsers = listOfFirstActivityResolveInfo(PackageInfoFakes.MiBrowser),
                apps = allAppsInfoList,
                isSingleOption = true,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @org.junit.Test
    fun `whitelisted browsers, but none selected`() {
        val config = BrowserModeConfigHelper.Whitelisted(null)

        val result = handler.filterBrowsers(config, false, allBrowsersResolveInfos, allResolvedInfoList)
        assertThat(result).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = config.mode,
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

        val result = handler.filterBrowsers(config, false, allBrowsersResolveInfos, allResolvedInfoList)
        assertThat(result).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = config.mode,
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

        val result = handler.filterBrowsers(config, false, allBrowsersResolveInfos, allResolvedInfoList)
        assertThat(result).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = config.mode,
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
