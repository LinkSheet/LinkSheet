package fe.linksheet.resolver

import android.content.pm.ResolveInfo
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.*
import assertk.assertions.*
import fe.linksheet.module.resolver.AutoLaunchSingleBrowserExperiment
import fe.linksheet.module.resolver.FilteredBrowserList
import fe.linksheet.module.resolver.BrowserModeConfigHelper
import fe.linksheet.module.resolver.browser.BrowserMode
import app.linksheet.testing.ResolveInfoFakes.allBrowsers
import app.linksheet.testing.ResolveInfoFakes.DuckDuckGoBrowser
import app.linksheet.testing.ResolveInfoFakes.MiBrowser
import app.linksheet.testing.ResolveInfoFakes.NewPipe
import app.linksheet.testing.ResolveInfoFakes.packageName
import app.linksheet.testing.ResolveInfoFakes.toKeyedMap
import app.linksheet.testing.ResolveInfoFakes.Youtube
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
            setOf(DuckDuckGoBrowser.packageName)
        )

        private val whitelistedMiBrowser = BrowserModeConfigHelper.Whitelisted(setOf(MiBrowser.packageName))
        private val whitelistedMiDuckDuckGoBrowser = BrowserModeConfigHelper.Whitelisted(
            setOf(DuckDuckGoBrowser.packageName, MiBrowser.packageName)
        )

        private val selectedNull = BrowserModeConfigHelper.SelectedBrowser(null)
        private val selectedDuckDuckGoBrowser = BrowserModeConfigHelper.SelectedBrowser(DuckDuckGoBrowser.packageName)
        private val selectedMiBrowser = BrowserModeConfigHelper.SelectedBrowser(MiBrowser.packageName)

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
        vararg browsers: ResolveInfo,
    ) {
        return isNotNull().all {
            prop(FilteredBrowserList::browserMode).isEqualTo(mode)
            prop(FilteredBrowserList::browsers).containsExactlyInAnyOrder(*browsers)
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
            AutoLaunchSingleBrowserExperiment.handle(config, listOf(Youtube), DuckDuckGoBrowser.toKeyedMap())
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isNull()
        }
    }

    @Test
    fun `single browser, many apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserExperiment.handle(config, listOf(Youtube, NewPipe), DuckDuckGoBrowser.toKeyedMap())
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isNull()
        }
    }

    @Test
    fun `single browser, no apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserExperiment.handle(config, emptyList(), DuckDuckGoBrowser.toKeyedMap())
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isValid(config.mode, DuckDuckGoBrowser)
        }
    }

    @Test
    fun `many browsers, no apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserExperiment.handle(config, emptyList(), allBrowsers.toKeyedMap())
        }

        assertAll {
            assertThat(runTest(BrowserModeConfigHelper.None)).isNull()
            assertThat(runTest(BrowserModeConfigHelper.AlwaysAsk)).isNull()
            assertThat(runTest(whitelistedNull)).isNull()
            assertThat(runTest(whitelistedEmpty)).isNull()
            assertThat(runTest(whitelistedMiDuckDuckGoBrowser)).isNull()
            assertThat(runTest(selectedNull)).isNull()

            assertThat(runTest(whitelistedDuckDuckGoBrowser)).isValid(BrowserMode.Whitelisted, DuckDuckGoBrowser)
            assertThat(runTest(whitelistedMiBrowser)).isValid(BrowserMode.Whitelisted, MiBrowser)
            assertThat(runTest(selectedDuckDuckGoBrowser)).isValid(BrowserMode.SelectedBrowser, DuckDuckGoBrowser)
            assertThat(runTest(selectedMiBrowser)).isValid(BrowserMode.SelectedBrowser, MiBrowser)
        }
    }

    @After
    fun teardown() = stopKoin()
}
