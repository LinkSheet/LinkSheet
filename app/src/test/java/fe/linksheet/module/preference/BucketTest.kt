package fe.linksheet.module.preference

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isCloseTo
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest
import fe.linksheet.util.percent
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class BucketTest : BaseUnitTest {
    @org.junit.Test
    fun test() {
        if (System.getenv("CI")?.toBooleanStrictOrNull() == true) {
            // Skip on CI since there is a chance it will fail
            return
        }

        val userCount = 5000
        val percent = 10.percent
        val buckets = 1_000_000L
        val flag = "experiment_improved_intent_resolver"

        val users = (0..userCount).map { it.toString() }

        val runs = 1000
        val eligibleCounts = sequence {
            (0..runs).forEach { run ->
                yield(users.count { user -> Bucket.isEligible(flag, user + run, percent, buckets) })
            }
        }

        // Given enough runs, we can expect the average of eligible users per run to be ~500
        assertThat(eligibleCounts.average()).isCloseTo(500.0, 1.0)
    }
}
