package fe.linksheet.resolver

import android.content.pm.ResolveInfo
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.*
import assertk.assertions.*
import fe.linksheet.experiment.improved.resolver.AutoLaunchSingleBrowserExperiment
import fe.linksheet.experiment.improved.resolver.FilteredBrowserList
import fe.linksheet.experiment.improved.resolver.browser.BrowserModeConfigHelper
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.resolver.util.ResolveInfos.allBrowsers
import fe.linksheet.resolver.util.ResolveInfos.duckduckgoBrowser
import fe.linksheet.resolver.util.ResolveInfos.miBrowser
import fe.linksheet.resolver.util.ResolveInfos.newPipe
import fe.linksheet.resolver.util.ResolveInfos.packageName
import fe.linksheet.resolver.util.ResolveInfos.toKeyedMap
import fe.linksheet.resolver.util.ResolveInfos.youtube
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
            setOf(duckduckgoBrowser.packageName)
        )

        private val whitelistedMiBrowser = BrowserModeConfigHelper.Whitelisted(setOf(miBrowser.packageName))
        private val whitelistedMiDuckDuckGoBrowser = BrowserModeConfigHelper.Whitelisted(
            setOf(duckduckgoBrowser.packageName, miBrowser.packageName)
        )

        private val selectedNull = BrowserModeConfigHelper.SelectedBrowser(null)
        private val selectedDuckDuckGoBrowser = BrowserModeConfigHelper.SelectedBrowser(duckduckgoBrowser.packageName)
        private val selectedMiBrowser = BrowserModeConfigHelper.SelectedBrowser(miBrowser.packageName)

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
            AutoLaunchSingleBrowserExperiment.handle(config, listOf(youtube), duckduckgoBrowser.toKeyedMap())
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isNull()
        }
    }

    @Test
    fun `single browser, many apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserExperiment.handle(config, listOf(youtube, newPipe), duckduckgoBrowser.toKeyedMap())
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isNull()
        }
    }

    @Test
    fun `single browser, no apps`() {
        val runTest: (BrowserModeConfigHelper) -> FilteredBrowserList? = { config ->
            AutoLaunchSingleBrowserExperiment.handle(config, emptyList(), duckduckgoBrowser.toKeyedMap())
        }

        assertEach(configs) { config ->
            assertThat(runTest(config)).isValid(config.mode, duckduckgoBrowser)
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

            assertThat(runTest(whitelistedDuckDuckGoBrowser)).isValid(BrowserMode.Whitelisted, duckduckgoBrowser)
            assertThat(runTest(whitelistedMiBrowser)).isValid(BrowserMode.Whitelisted, miBrowser)
            assertThat(runTest(selectedDuckDuckGoBrowser)).isValid(BrowserMode.SelectedBrowser, duckduckgoBrowser)
            assertThat(runTest(selectedMiBrowser)).isValid(BrowserMode.SelectedBrowser, miBrowser)
        }
    }

    @After
    fun teardown() = stopKoin()
}
