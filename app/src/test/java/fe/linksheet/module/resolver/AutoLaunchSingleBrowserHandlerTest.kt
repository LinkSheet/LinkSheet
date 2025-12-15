package fe.linksheet.module.resolver

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.util.PackageInfoFake
import app.linksheet.testing.util.firstActivityResolveInfo
import app.linksheet.testing.util.listOfFirstActivityResolveInfo
import app.linksheet.testing.util.packageName
import assertk.Assert
import assertk.all
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.*
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class AutoLaunchSingleBrowserHandlerTest : BaseUnitTest  {
    companion object {
        private val whitelistedNull = BrowserModeConfigHelper.Whitelisted(null)
        private val whitelistedEmpty = BrowserModeConfigHelper.Whitelisted(emptySet())
        private val whitelistedDuckDuckGoBrowser = BrowserModeConfigHelper.Whitelisted(
            setOf(PackageInfoFakes.DuckDuckGoBrowser.packageName)
        )

        private val whitelistedMiBrowser =
            BrowserModeConfigHelper.Whitelisted(setOf(PackageInfoFakes.MiBrowser.packageName))
        private val whitelistedMiDuckDuckGoBrowser = BrowserModeConfigHelper.Whitelisted(
            setOf(PackageInfoFakes.DuckDuckGoBrowser.packageName, PackageInfoFakes.MiBrowser.packageName)
        )

        private val selectedNull = BrowserModeConfigHelper.SelectedBrowser(null)
        private val selectedDuckDuckGoBrowser =
            BrowserModeConfigHelper.SelectedBrowser(PackageInfoFakes.DuckDuckGoBrowser.packageName)
        private val selectedMiBrowser = BrowserModeConfigHelper.SelectedBrowser(PackageInfoFakes.MiBrowser.packageName)

        val configs = listOf(
            BrowserModeConfigHelper.None,
            BrowserModeConfigHelper.AlwaysAsk,
            whitelistedNull,
            whitelistedEmpty,
            whitelistedDuckDuckGoBrowser,
            whitelistedMiBrowser,
            whitelistedMiDuckDuckGoBrowser,
            selectedNull,
            selectedDuckDuckGoBrowser,
            selectedMiBrowser,
        )
    }

    private fun Assert<FilteredBrowserList?>.isValid(
        mode: BrowserMode,
        vararg browsers: PackageInfoFake,
    ) {
        val resolveInfos = listOfFirstActivityResolveInfo(*browsers)

        return isNotNull().all {
            prop(FilteredBrowserList::browserMode).isEqualTo(mode)
            prop(FilteredBrowserList::browsers).containsExactlyInAnyOrder(*resolveInfos.toTypedArray())
            prop(FilteredBrowserList::apps).isEmpty()
            prop(FilteredBrowserList::isSingleOption).isEqualTo(true)
            prop(FilteredBrowserList::noBrowsersOnlySingleApp).isEqualTo(false)
        }
    }

    private inline fun <T> assertEach(iterable: Iterable<T>, f: (T) -> Unit) {
        assertAll {
            for (element in iterable) {
                f(element)
            }
        }
    }

    @org.junit.Test
    fun `no browsers, no apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserHandler.handle(config, emptyList(), emptyList())
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isNull()
        }
    }

    @org.junit.Test
    fun `single browser, single app`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserHandler.handle(
                config,
                listOfFirstActivityResolveInfo(PackageInfoFakes.Youtube),
                listOfNotNull(PackageInfoFakes.DuckDuckGoBrowser.firstActivityResolveInfo)
            )
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isNull()
        }
    }

    @org.junit.Test
    fun `single browser, many apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserHandler.handle(
                config,
                listOfFirstActivityResolveInfo(PackageInfoFakes.Youtube, PackageInfoFakes.NewPipe),
                listOfNotNull(PackageInfoFakes.DuckDuckGoBrowser.firstActivityResolveInfo)
            )
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isNull()
        }
    }

    @org.junit.Test
    fun `single browser, no apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserHandler.handle(
                config,
                emptyList(),
                listOfNotNull(PackageInfoFakes.DuckDuckGoBrowser.firstActivityResolveInfo)
            )
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isValid(config.mode, PackageInfoFakes.DuckDuckGoBrowser)
        }
    }

    @org.junit.Test
    fun `many browsers, no apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserHandler.handle(config, emptyList(), ImprovedBrowserHandlerTest.allBrowsersResolveInfos)
        }

        assertAll {
            assertThat(runTest(BrowserModeConfigHelper.None)).isNull()
            assertThat(runTest(BrowserModeConfigHelper.AlwaysAsk)).isNull()
            assertThat(runTest(whitelistedNull)).isNull()
            assertThat(runTest(whitelistedEmpty)).isNull()
            assertThat(runTest(whitelistedMiDuckDuckGoBrowser)).isNull()
            assertThat(runTest(selectedNull)).isNull()

            assertThat(runTest(whitelistedDuckDuckGoBrowser)).isValid(
                BrowserMode.Whitelisted,
                PackageInfoFakes.DuckDuckGoBrowser
            )
            assertThat(runTest(whitelistedMiBrowser)).isValid(BrowserMode.Whitelisted, PackageInfoFakes.MiBrowser)
            assertThat(runTest(selectedDuckDuckGoBrowser)).isValid(
                BrowserMode.SelectedBrowser,
                PackageInfoFakes.DuckDuckGoBrowser
            )
            assertThat(runTest(selectedMiBrowser)).isValid(BrowserMode.SelectedBrowser, PackageInfoFakes.MiBrowser)
        }
    }
}
