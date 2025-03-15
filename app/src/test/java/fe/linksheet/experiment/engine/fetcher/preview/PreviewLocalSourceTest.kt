package fe.linksheet.experiment.engine.fetcher.preview

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.UnitTest
import fe.std.result.assert.assertSuccess
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class PreviewLocalSourceTest : UnitTest {
    companion object {
        private const val URL = "https://linksheet.app"
    }

    @Test
    fun `bad request`() = runTest {
        val mockEngine = MockEngine {
            respondBadRequest()
        }

        val source = PreviewLocalSource(HttpClient(mockEngine))
        val result = source.fetch(URL)

        assertSuccess(result)
            .isInstanceOf<PreviewResult.NonHtmlPage>()
            .prop(PreviewResult.NonHtmlPage::url)
            .isEqualTo(URL)
    }
}
