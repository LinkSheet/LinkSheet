package fe.linksheet.resolver

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.*
import assertk.Assert
import assertk.all
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.*
import fe.linksheet.module.resolver.AutoLaunchSingleBrowserExperiment
import fe.linksheet.module.resolver.BrowserModeConfigHelper
import fe.linksheet.module.resolver.FilteredBrowserList
import fe.linksheet.module.resolver.browser.BrowserMode
import org.junit.After
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class ExperimentAutoLaunchSingleBrowserTest {
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

    @Test
    fun `no browsers, no apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserExperiment.handle(config, emptyList(), emptyMap())
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isNull()
        }
    }

    @Test
    fun `single browser, single app`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserExperiment.handle(
                config,
                listOfFirstActivityResolveInfo(PackageInfoFakes.Youtube),
                PackageInfoFakes.DuckDuckGoBrowser.toKeyedMap()
            )
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isNull()
        }
    }

    @Test
    fun `single browser, many apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserExperiment.handle(
                config,
                listOfFirstActivityResolveInfo(PackageInfoFakes.Youtube, PackageInfoFakes.NewPipe),
                PackageInfoFakes.DuckDuckGoBrowser.toKeyedMap()
            )
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isNull()
        }
    }

    @Test
    fun `single browser, no apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserExperiment.handle(
                config,
                emptyList(),
                PackageInfoFakes.DuckDuckGoBrowser.toKeyedMap()
            )
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isValid(config.mode, PackageInfoFakes.DuckDuckGoBrowser)
        }
    }

    @Test
    fun `many browsers, no apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserExperiment.handle(config, emptyList(), PackageInfoFakes.allBrowsers.toKeyedMap())
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

    @After
    fun teardown() = stopKoin()
}
