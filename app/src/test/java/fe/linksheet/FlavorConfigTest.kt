package fe.linksheet

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import fe.linksheet.util.buildconfig.FlavorConfig
import kotlin.test.Test

internal class FlavorConfigTest {
    @Test
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
                assertThat(FlavorConfig.parseFlavorConfig(config)).isEqualTo(expected)
            }
    }
}
