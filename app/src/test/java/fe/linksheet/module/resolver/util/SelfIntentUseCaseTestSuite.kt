package fe.linksheet.module.resolver.util

import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import de.infix.testBalloon.framework.core.TestConfig
import de.infix.testBalloon.framework.core.testSuite
import de.infix.testBalloon.integration.robolectric.ApplicationLifetime
import de.infix.testBalloon.integration.robolectric.RobolectricTestSuiteContent
import de.infix.testBalloon.integration.robolectric.robolectric
import de.infix.testBalloon.integration.robolectric.robolectricTestSuite
import fe.composekit.intent.buildIntent
import fe.linksheet.module.resolver.SelfIntentUseCase

val SelfIntentUseCaseTestSuite by testSuite {
//    for (apiLevel in Build.VERSION_CODES.O..Build.VERSION_CODES.BAKLAVA) {
    for (apiLevel in listOf(Build.VERSION_CODES.BAKLAVA)) {
        robolectricTestSuite<SelfIntentUseCaseTestSuiteImpl>(
            "API level $apiLevel",
            testConfig = TestConfig.robolectric {
                sdk = apiLevel
                qualifiers = "xlarge-port"
                applicationLifetime = ApplicationLifetime.RobolectricTestSuite
            }
        )
    }
}

internal class SelfIntentUseCaseTestSuiteImpl : RobolectricTestSuiteContent({
    val useCase = SelfIntentUseCase("fe.linksheet")
    val customTabIntent = buildIntent(Intent.ACTION_VIEW, "http://developer.chrome.com".toUri()) {
        putExtra("android.support.customtabs.extra.SESSION", null as String?)
        putExtra("androidx.browser.customtabs.extra.SHARE_STATE", 0)
        putExtra("org.chromium.chrome.browser.customtabs.EXTRA_DISABLE_STAR_BUTTON", true)
        putExtra("some.other.extra", true)
    }

    test("custom tab extras are removed, others are kept") {
        val result = useCase.buildOpenExternallyIntent(customTabIntent)
        assertThat(result).all {
            prop(Intent::getAction).isEqualTo(Intent.ACTION_VIEW)
            prop(Intent::getData).isEqualTo("http://developer.chrome.com".toUri())
            prop(Intent::getPackage).isEqualTo("fe.linksheet")
            transform { it.extras }.all {
                transform { it?.size() }.isEqualTo(1)
                transform { it?.getBoolean("some.other.extra") }.isEqualTo(true)
            }
        }
    }
})
