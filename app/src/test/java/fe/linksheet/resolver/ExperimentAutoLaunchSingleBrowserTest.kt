package fe.linksheet.resolver

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isDataClassEqualTo
import fe.linksheet.experiment.improved.resolver.FilteredBrowserList
import fe.linksheet.experiment.improved.resolver.browser.BrowserModeConfigHelper
import fe.linksheet.experiment.improved.resolver.browser.ImprovedBrowserHandler
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.resolver.util.ResolveInfos.duckduckgoBrowser
import fe.linksheet.resolver.util.ResolveInfos.toKeyedMap
import org.junit.After
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class ExperimentAutoLaunchSingleBrowserTest {
    companion object {
        private val handler = ImprovedBrowserHandler()
    }

    @Test
    fun `single browser, no apps`() {
        val config = BrowserModeConfigHelper.None

        assertThat(
            handler.filterBrowsers(
                config,
                duckduckgoBrowser.toKeyedMap(),
                emptyList(),
                autoLaunchSingleBrowserExperiment = true
            )
        ).isDataClassEqualTo(
            FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = listOf(duckduckgoBrowser),
                apps = emptyList(),
                isSingleOption = true,
                noBrowsersOnlySingleApp = false
            )
        )
    }

    @After
    fun teardown() = stopKoin()
}
