package fe.linksheet.module.app.`package`

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.PackageInfoFakes
import app.linksheet.testing.YatsePackageInfoFake
import app.linksheet.testing.flatResolveInfos
import assertk.assertThat
import assertk.assertions.containsExactly
import fe.linksheet.extension.android.info
import org.junit.After
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class PackageIntentHandlerTest {

    @Test
    fun test() {
        val handler: PackageIntentHandler = DefaultPackageIntentHandler(
            queryIntentActivities = { _, _ -> YatsePackageInfoFake.resolveInfos },
            isLinkSheetCompat = { false },
            checkReferrerExperiment = { true },
            checkDisableDeduplicationExperiment = { true },
        )

        val handlers = handler.findHandlers(Uri.parse("https://www.youtube.com/watch?v=evIpx9Onc2c"), null)
        val infos = handlers.map {
            with(it.info) { "$packageName/$name" }
        }

        assertThat(infos).containsExactly(
            "org.leetzone.android.yatsewidgetfree/org.leetzone.android.yatsewidget.ui.activity.SendToActivity",
            "org.leetzone.android.yatsewidgetfree/org.leetzone.android.yatsewidgetfree.QueueToActivity"
        )
    }

    @Test
    fun test2() {
        val handler: PackageIntentHandler = DefaultPackageIntentHandler(
            queryIntentActivities = { _, _ -> PackageInfoFakes.allResolved.flatResolveInfos() },
            isLinkSheetCompat = { false },
            checkReferrerExperiment = { true },
            checkDisableDeduplicationExperiment = { true },
        )

//        val handlers = handler.findHandlers(Uri.parse("https://www.youtube.com/watch?v=evIpx9Onc2c"), null)
//        handlers
    }

    @After
    fun teardown() = stopKoin()
}
