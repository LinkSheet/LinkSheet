package fe.linksheet.module.resolver

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.test.Table2Builder
import fe.std.test.TestFunction
import fe.std.test.tableTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class ImprovedIntentResolverTest : BaseUnitTest {
    private data class Input(
        val enabled: Boolean,
        val mode: FollowRedirectsMode,
        val skipBrowser: Boolean,
        val isReferrerBrowser: Boolean,
        val hasManualFlag: Boolean
    )

    @Test
    fun `test shouldFollowRedirects`() = dataTest(
        data = tableTest<Input, Boolean>("input", "result")
            .row(
                column1 = Input(
                    enabled = true,
                    mode = FollowRedirectsMode.Auto,
                    skipBrowser = false,
                    isReferrerBrowser = false,
                    hasManualFlag = false
                ),
                column2 = true
            )
            .row(
                column1 = Input(
                    enabled = false,
                    mode = FollowRedirectsMode.Auto,
                    skipBrowser = false,
                    isReferrerBrowser = false,
                    hasManualFlag = false
                ),
                column2 = false
            )
            .row(
                column1 = Input(
                    enabled = true,
                    mode = FollowRedirectsMode.Auto,
                    skipBrowser = true,
                    isReferrerBrowser = false,
                    hasManualFlag = false
                ),
                column2 = true
            )
            .row(
                column1 = Input(
                    enabled = true,
                    mode = FollowRedirectsMode.Auto,
                    skipBrowser = true,
                    isReferrerBrowser = true,
                    hasManualFlag = false
                ),
                column2 = false
            )
            .row(
                column1 = Input(
                    enabled = true,
                    mode = FollowRedirectsMode.Auto,
                    skipBrowser = true,
                    isReferrerBrowser = true,
                    hasManualFlag = true
                ),
                column2 = false
            ),
        test = { (enabled, mode, skipBrowser, isReferrerBrowser, hasManualFlag) ->
            ImprovedIntentResolver.shouldFollowRedirects(enabled, mode, skipBrowser, isReferrerBrowser, hasManualFlag)
        },
        assert = { result, expected ->
            assertThat(result).isEqualTo(expected)
        }
    )
}

fun <I, R> dataTest(
    data: Table2Builder<I, R>,
    test: TestFunction<I, R>,
    assert: (R, R) -> Unit
) {
    val wrapper = data.test2(test)
    wrapper.forAll { input, expected ->
        val result = runTest(input)
        assert(result, expected)
    }
}
