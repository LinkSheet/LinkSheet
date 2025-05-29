package fe.linksheet.util.buildconfig

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import fe.kotlin.extension.string.encodeBase64OrNull
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest
import kotlin.io.encoding.ExperimentalEncodingApi

internal class FlavorConfigTest : BaseUnitTest {
    @OptIn(ExperimentalEncodingApi::class)
    @JunitTest
    fun test() {
        tableOf("config", "expected")
            .row<String?, FlavorConfig>(null, FlavorConfig.Default)
            .row("", FlavorConfig.Default)
            .row("""{"isPro": false}""", FlavorConfig.Default)
            .row("{", FlavorConfig.Default)
            .row(
                """{"isPro": true, "supabaseHost": "Host", "supabaseApiKey": "ApiKey"}""",
                FlavorConfig(true, "Host", "ApiKey")
            )
            .forAll { config, expected ->
                assertThat(FlavorConfig.parseFlavorConfig(config?.encodeBase64OrNull())).isEqualTo(expected)
            }
    }
}
