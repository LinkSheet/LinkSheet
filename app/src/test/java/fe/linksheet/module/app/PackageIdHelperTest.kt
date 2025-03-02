package fe.linksheet.module.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.YatsePackageInfoFake
import app.linksheet.testing.util.asDescriptors
import assertk.assertThat
import assertk.assertions.containsExactly
import fe.linksheet.BaseUnitTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class PackageIdHelperTest : BaseUnitTest {
    @Test
    fun `test activity descriptors`() {
        val result = YatsePackageInfoFake.resolveInfos.asDescriptors()

        assertThat(result).containsExactly(
            "org.leetzone.android.yatsewidgetfree/org.leetzone.android.yatsewidget.ui.activity.SendToActivity:",
            "org.leetzone.android.yatsewidgetfree/.QueueToActivity:org.leetzone.android.yatsewidget.ui.activity.SendToActivity"
        )
    }
}
