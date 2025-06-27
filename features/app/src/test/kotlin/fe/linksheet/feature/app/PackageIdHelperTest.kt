package fe.linksheet.feature.app

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.YatsePackageInfoFake
import app.linksheet.testing.fake.asDescriptors
import assertk.assertThat
import assertk.assertions.containsExactly
import fe.linksheet.testlib.core.RobolectricTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class PackageIdHelperTest : RobolectricTest {
    @Test
    fun `test activity descriptors`() {
        val result = YatsePackageInfoFake.resolveInfos.asDescriptors()

        assertThat(result).containsExactly(
            "org.leetzone.android.yatsewidgetfree/org.leetzone.android.yatsewidget.ui.activity.SendToActivity:",
            "org.leetzone.android.yatsewidgetfree/.QueueToActivity:org.leetzone.android.yatsewidget.ui.activity.SendToActivity"
        )
    }
}
