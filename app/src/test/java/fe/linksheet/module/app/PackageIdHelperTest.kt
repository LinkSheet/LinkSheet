package fe.linksheet.module.app

import app.linksheet.testing.fake.YatsePackageInfoFake
import app.linksheet.testing.util.asDescriptors
import assertk.assertThat
import assertk.assertions.containsExactly
import fe.linksheet.BaseUnitTest
import kotlin.test.Test

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
