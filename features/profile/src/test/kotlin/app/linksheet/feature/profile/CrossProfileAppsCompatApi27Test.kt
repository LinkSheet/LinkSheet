package app.linksheet.feature.profile

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.profile.core.CrossProfileAppsCompat
import assertk.assertThat
import assertk.assertions.isEmpty
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O])
internal class CrossProfileAppsCompatApi27Test : BaseUnitTest {
    @Test
    fun compatUsesNoOpOnUnsupportedApiLevel() {
        val compat = CrossProfileAppsCompat(applicationContext)
        assertThat(compat.getTargetUserProfiles()).isEmpty()
    }
}
