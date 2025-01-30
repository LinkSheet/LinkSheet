package fe.linksheet.module.app.`package`

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.YatsePackageInfoFake
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class PackageIntentHandlerTest {
    @Test
    fun test() {
        val handler: PackageIntentHandler = DefaultPackageIntentHandler(
            queryIntentActivities = { _, _ ->
                YatsePackageInfoFake.resolveInfos
            },
            isLinkSheetCompat = { false },
            checkReferrerExperiment = { true },
            checkDisableDeduplicationExperiment = { true },
        )

        val handlers = handler.findHandlers(Uri.parse("https://www.youtube.com/watch?v=evIpx9Onc2c"), null)

        // TODO: Finish test
//        println(handlers)
    }
}
