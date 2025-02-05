package fe.linksheet.module.app.`package`

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.TurretPackageInfoFake
import app.linksheet.testing.fake.YatsePackageInfoFake
import app.linksheet.testing.util.asDescriptors
import assertk.assertThat
import assertk.assertions.containsExactly
import fe.linksheet.UnitTest
import fe.linksheet.extension.android.info
import fe.linksheet.UnitTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class PackageIntentHandlerTest : UnitTest {

    @Test
    fun `test trampoline activity correctly handled`() {
        val handler: PackageIntentHandler = DefaultPackageIntentHandler(
            queryIntentActivities = { _, _ -> YatsePackageInfoFake.resolveInfos },
            isLinkSheetCompat = { false },
            checkReferrerExperiment = { true },
        )

        val handlers = handler.findHandlers(Uri.parse("https://www.youtube.com/watch?v=evIpx9Onc2c"), null)

        assertThat(handlers.asDescriptors()).containsExactly(
            "org.leetzone.android.yatsewidgetfree/org.leetzone.android.yatsewidget.ui.activity.SendToActivity:",
            "org.leetzone.android.yatsewidgetfree/.QueueToActivity:org.leetzone.android.yatsewidget.ui.activity.SendToActivity"
        )
    }

    @Test
    fun `test non-exported activities are ignored`() {
        val handler: PackageIntentHandler = DefaultPackageIntentHandler(
            queryIntentActivities = { _, _ -> TurretPackageInfoFake.resolveInfos },
            isLinkSheetCompat = { false },
            checkReferrerExperiment = { true },
        )

        val handlers = handler.findHandlers(Uri.parse("https://t.me/magiskalpha"), null)
        assertThat(handlers.asDescriptors()).containsExactly(
            "org.telegram.group/org.telegram.ui.LaunchActivity:"
        )
    }
}
