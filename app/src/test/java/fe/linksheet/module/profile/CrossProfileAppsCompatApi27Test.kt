package fe.linksheet.module.profile

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEmpty
import fe.linksheet.UnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O])
internal class CrossProfileAppsCompatApi27Test : UnitTest {
    @Test
    fun `compat uses no-op on unsupported api level`() {
        val compat = CrossProfileAppsCompat(applicationContext)
        assertThat(compat.getTargetUserProfiles()).isEmpty()
    }
}
