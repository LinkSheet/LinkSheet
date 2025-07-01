package fe.linksheet.module.resolver.util

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isTrue
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.util.IntentFlags
import fe.linksheet.util.intent.buildIntent
import mozilla.components.support.utils.toSafeIntent
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class IntentSanitizerTest : BaseUnitTest {
    @JunitTest
    fun test() {
        val flags = (Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                or Intent.FLAG_ACTIVITY_FORWARD_RESULT
                or Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        val intent = buildIntent { this@buildIntent.flags = flags }.toSafeIntent()
        val sanitized = IntentSanitizer.sanitize(intent, Intent.ACTION_VIEW, null, null)

        assertThat(sanitized.flags)
            .transform { !IntentFlags.ACTIVITY_EXCLUDE_FROM_RECENTS.contains(it) }
            .isTrue()
        assertThat(sanitized.flags)
            .transform { !IntentFlags.ACTIVITY_FORWARD_RESULT.contains(it) }
            .isTrue()
    }
}
