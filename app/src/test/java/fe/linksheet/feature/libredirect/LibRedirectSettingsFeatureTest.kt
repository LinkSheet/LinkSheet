package fe.linksheet.feature.libredirect

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import fe.libredirectkt.LibRedirectFrontend
import fe.libredirectkt.LibRedirectInstance
import fe.libredirectkt.LibRedirectService
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

internal class LibRedirectSettingsFeatureTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()
    private val loadBuiltInServices = listOf(
        LibRedirectService(
            key = "reddit",
            name = "Reddit",
            url = "https://reddit.com",
            frontends = listOf(
                LibRedirectFrontend(
                    key = "libreddit",
                    name = "Libreddit",
                    excludeTargets = emptyList(),
                    url = "https://github.com/spikecodes/libreddit"
                ),
                LibRedirectFrontend(
                    key = "redlib",
                    name = "Redlib",
                    excludeTargets = emptyList(),
                    url = "https://github.com/redlib-org/redlib"
                ),
                LibRedirectFrontend(
                    key = "teddit",
                    name = "Teddit",
                    excludeTargets = emptyList(),
                    url = "https://codeberg.org/teddit/teddit"
                ),
                LibRedirectFrontend(
                    key = "eddrit",
                    name = "Eddrit",
                    excludeTargets = emptyList(),
                    url = "https://github.com/corenting/eddrit"
                ),
                LibRedirectFrontend(
                    key = "troddit",
                    name = "Troddit",
                    excludeTargets = emptyList(),
                    url = "https://github.com/burhan-syed/troddit"
                )
            ),
            defaultFrontend = LibRedirectFrontend(
                key = "libreddit",
                name = "Libreddit",
                excludeTargets = emptyList(),
                url = "https://github.com/spikecodes/libreddit"
            ),
            targets = listOf(
                """^https?:\/{2}(www\.|old\.|np\.|new\.|amp\.)?(reddit|reddittorjg6rue252oqsxryoxengawnmo46qy4kyii5wtqnwfj4ooad)\.(com|onion)(?=\/u(ser)?\/|\/r\/|\/search|\/new|\/comments|\/?$)""".toRegex(),
                """^https?:\/{2}((i|(external-)?preview)\.)?redd\.it""".toRegex()
            )
        )
    )

    private val loadBuiltInInstances = listOf(
        LibRedirectInstance(
            frontendKey = "libreddit",
            hosts = listOf(
                "https://redlib.catsarch.com",
                "https://redlib.perennialte.ch",
                "https://redlib.tux.pizza",
                "https://rl.bloat.cat",
                "https://redlib.r4fo.com",
                "https://reddit.owo.si",
                "https://redlib.ducks.party",
                "https://red.artemislena.eu",
                "https://redlib.nadeko.net",
                "https://redlib.private.coffee",
                "https://redlib.4o1x5.dev"
            )
        )
    )

    @org.junit.Test
    fun test() = runTest(dispatcher) {
        val feature = LibRedirectSettingsFeature(
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
