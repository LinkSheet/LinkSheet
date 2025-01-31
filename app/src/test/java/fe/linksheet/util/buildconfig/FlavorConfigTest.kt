package fe.linksheet.util.buildconfig

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import fe.kotlin.extension.string.encodeBase64OrNull
import fe.linksheet.LinkSheetTest
import org.junit.runner.RunWith
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class FlavorConfigTest : LinkSheetTest{
    @OptIn(ExperimentalEncodingApi::class)
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
                assertThat(FlavorConfig.parseFlavorConfig(config?.encodeBase64OrNull())).isEqualTo(expected)
            }
    }
}
