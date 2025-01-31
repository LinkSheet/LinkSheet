package fe.linksheet.module.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.YatsePackageInfoFake
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.LinkSheetTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class PackageKeyServiceTest : LinkSheetTest {
    @Test
    fun test() {
        val packageKey = PackageKeyService(
            checkDisableDeduplicationExperiment = { true }
        )
        val result = YatsePackageInfoFake.resolveInfos.mapToSet {
            packageKey.getDuplicationKey(it.activityInfo)
        }

        assertThat(result).isEqualTo(
            setOf(
                "org.leetzone.android.yatsewidgetfree/org.leetzone.android.yatsewidget.ui.activity.SendToActivity:",
                "org.leetzone.android.yatsewidgetfree/.QueueToActivity:org.leetzone.android.yatsewidget.ui.activity.SendToActivity"
            )
        )
    }
}
