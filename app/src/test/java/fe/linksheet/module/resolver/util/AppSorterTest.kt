@file:OptIn(ExperimentalTime::class)

package fe.linksheet.module.resolver.util

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.asPreferredApp
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.fake.asDescriptor
import app.linksheet.testing.fake.toActivityAppInfo
import app.linksheet.testing.util.listOfFirstActivityResolveInfo
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import app.linksheet.feature.app.core.ActivityAppInfo
import fe.linksheet.module.resolver.FilteredBrowserList
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class AppSorterTest : BaseUnitTest {
    companion object {
        private val sorter = AppSorter(
            queryAndAggregateUsageStats = { _, _ -> emptyMap() },
            toActivityAppInfo = { resolveInfo, browser -> resolveInfo.toActivityAppInfo() },
            clock = Clock.System
        )
        private val allAppsInfoList = listOfFirstActivityResolveInfo(PackageInfoFakes.allApps)
        private val allBrowsersInfoList = listOfFirstActivityResolveInfo(PackageInfoFakes.allBrowsers)
        private val appList = FilteredBrowserList(
            browserMode = BrowserMode.AlwaysAsk,
            browsers = allBrowsersInfoList,
            apps = allAppsInfoList,
            isSingleOption = false,
            noBrowsersOnlySingleApp = false
        )
        private val appAlwaysPreferred = PackageInfoFakes.MiBrowser.asPreferredApp("google.com", true)
        private val app = PackageInfoFakes.MiBrowser.asPreferredApp("google.com", false)
    }

    @org.junit.Test
    fun `returnLastChosen true, alwaysPreferred false`() {
        val (_, filtered) = sorter.sort(appList, app, emptyMap(), false)
        assertThat(filtered).isNull()
    }

    @org.junit.Test
    fun `alwaysPreferred true`() {
        fun runTest(returnLastChosen: Boolean): ActivityAppInfo? {
            val (_, filtered) = sorter.sort(appList, appAlwaysPreferred, emptyMap(), returnLastChosen)
            return filtered
        }

        fun ActivityAppInfo?.assert() {
            assertThat(this)
                .isNotNull()
                .transform { it.asDescriptor() }
                .isEqualTo(PackageInfoFakes.MiBrowser.toActivityAppInfo().asDescriptor())
        }

        runTest(true).assert()
        runTest(false).assert()
    }
}
