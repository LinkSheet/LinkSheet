package fe.linksheet.module.profile

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEmpty
import fe.linksheet.feature.profile.CrossProfileAppsCompat
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O])
internal class CrossProfileAppsCompatApi27Test : BaseUnitTest  {
    @org.junit.Test
    fun compatUsesNoOpOnUnsupportedApiLevel() {
        val compat = CrossProfileAppsCompat(applicationContext)
        assertThat(compat.getTargetUserProfiles()).isEmpty()
    }
}
