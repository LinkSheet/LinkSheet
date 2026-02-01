package app.linksheet.feature.libredirect

import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class LibRedirectSettingsFeatureTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()
    private val loadBuiltInServices = listOf(LibRedirectData.RedditService)
    private val loadBuiltInInstances = listOf(LibRedirectData.LibRedditInstance)

    @Test
    fun test() = runTest(dispatcher) {
        val feature = SettingsController(
            loadBuiltInServices = { loadBuiltInServices },
            loadBuiltInInstances = { loadBuiltInInstances },
            ioDispatcher = dispatcher
        )
        val settings = feature.loadSettings("reddit")
        assertThat(settings).isNotNull().all {
            prop(ServiceSettings::defaultFrontend).isNotNull().prop(FrontendState::frontendKey).isEqualTo("libreddit")
            prop(ServiceSettings::fallback).isNotNull().all {
                prop(LibRedirectDefault::serviceKey).isEqualTo("reddit")
                prop(LibRedirectDefault::frontendKey).isEqualTo("libreddit")
                prop(LibRedirectDefault::instanceUrl).isEqualTo("https://redlib.catsarch.com")
            }
        }
    }
}
