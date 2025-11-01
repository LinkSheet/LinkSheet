package app.linksheet.feature.app.pkg

import android.net.Uri
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.app.pkg.DefaultPackageIntentHandler
import app.linksheet.feature.app.pkg.PackageIntentHandler
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.fake.TurretPackageInfoFake
import app.linksheet.testing.fake.YatsePackageInfoFake
import app.linksheet.testing.fake.asDescriptors
import app.linksheet.testing.util.flatResolveInfos
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isNotNull
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class PackageIntentHandlerTest : BaseUnitTest  {

    @org.junit.Test
    fun `test trampoline activity correctly handled`() {
        val handler: PackageIntentHandler = DefaultPackageIntentHandler(
            queryIntentActivities = { _, _ -> YatsePackageInfoFake.resolveInfos },
            resolveActivity = { _, _ -> null },
            isLinkSheetCompat = { false },
            isSelf = { false },
            checkReferrerExperiment = { true },
        )

        val handlers = handler.findHandlers(Uri.parse("https://www.youtube.com/watch?v=evIpx9Onc2c"), null)

        assertThat(handlers.asDescriptors()).containsExactly(
            "org.leetzone.android.yatsewidgetfree/org.leetzone.android.yatsewidget.ui.activity.SendToActivity:",
            "org.leetzone.android.yatsewidgetfree/.QueueToActivity:org.leetzone.android.yatsewidget.ui.activity.SendToActivity"
        )
    }

    @org.junit.Test
    fun `test non-exported activities are ignored`() {
        val handler: PackageIntentHandler = DefaultPackageIntentHandler(
            queryIntentActivities = { _, _ -> TurretPackageInfoFake.resolveInfos },
            resolveActivity = { _, _ -> null },
            isLinkSheetCompat = { false },
            isSelf = { false },
            checkReferrerExperiment = { true },
        )

        val handlers = handler.findHandlers(Uri.parse("https://t.me/magiskalpha"), null)
        assertThat(handlers.asDescriptors()).containsExactly(
            "org.telegram.group/org.telegram.ui.LaunchActivity:"
        )
    }

    @org.junit.Test
    fun `test find all http browsable`() {
        val handler: PackageIntentHandler = DefaultPackageIntentHandler(
            queryIntentActivities = { _, _ -> PackageInfoFakes.allBrowsers.flatResolveInfos() },
            resolveActivity = { _, _ -> null },
            isLinkSheetCompat = { false },
            isSelf = { false },
            checkReferrerExperiment = { true },
        )

        assertThat(handler.findHttpBrowsable(null))
            .isNotNull()
            .transform { it.asDescriptors() }
            .containsExactly(
                "com.mi.globalbrowser/com.sec.android.app.sbrowser.SBrowserLauncherActivity:",
                "com.duckduckgo.mobile.android/com.duckduckgo.app.dispatchers.IntentDispatcherActivity:",
                "com.android.chrome/com.google.android.apps.chrome.Main:"
            )
    }
}
