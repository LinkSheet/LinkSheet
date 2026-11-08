package fe.linksheet.module.resolver.util

import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import fe.composekit.intent.buildIntent
import fe.linksheet.module.resolver.SelfIntentUseCase
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.BAKLAVA])
class SelfIntentUseCaseTest : BaseUnitTest {
    companion object {
        private val useCase = SelfIntentUseCase("fe.linksheet")
        private val customTabIntent = buildIntent(Intent.ACTION_VIEW, "http://developer.chrome.com".toUri()) {
            putExtra("android.support.customtabs.extra.SESSION", null as String?)
            putExtra("androidx.browser.customtabs.extra.SHARE_STATE", 0)
            putExtra("org.chromium.chrome.browser.customtabs.EXTRA_DISABLE_STAR_BUTTON", true)
            putExtra("some.other.extra", true)
        }
    }

    @org.junit.Test
    fun `custom tab extras are removed, others are kept`() {
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
}

//val SelfIntentUseCaseTestSuite by testSuite(testConfig = TestConfig.baseTest()) {
////    for (apiLevel in Build.VERSION_CODES.O..Build.VERSION_CODES.BAKLAVA) {
////    for (apiLevel in listOf(Build.VERSION_CODES.BAKLAVA)) {
//
//    val apiLevel = Build.VERSION_CODES.UPSIDE_DOWN_CAKE
//    robolectricTestSuite<SelfIntentUseCaseTestSuiteImpl>(
//        "API level $apiLevel",
//        testConfig = TestConfig.robolectric {
//            sdk = apiLevel
//            qualifiers = "xlarge-port"
//            applicationLifetime = ApplicationLifetime.Test
//        }
//    )
////    }
//}

//internal class SelfIntentUseCaseTestSuiteImpl : RobolectricTestSuiteContent({
//    val useCase = SelfIntentUseCase("fe.linksheet")
//    val customTabIntent = buildIntent(Intent.ACTION_VIEW, "http://developer.chrome.com".toUri()) {
//        putExtra("android.support.customtabs.extra.SESSION", null as String?)
//        putExtra("androidx.browser.customtabs.extra.SHARE_STATE", 0)
//        putExtra("org.chromium.chrome.browser.customtabs.EXTRA_DISABLE_STAR_BUTTON", true)
//        putExtra("some.other.extra", true)
//    }
//
//    test("custom tab extras are removed, others are kept") {
//        val result = useCase.buildOpenExternallyIntent(customTabIntent)
//        assertThat(result).all {
//            prop(Intent::getAction).isEqualTo(Intent.ACTION_VIEW)
//            prop(Intent::getData).isEqualTo("http://developer.chrome.com".toUri())
//            prop(Intent::getPackage).isEqualTo("fe.linksheet")
//            transform { it.extras }.all {
//                transform { it?.size() }.isEqualTo(1)
//                transform { it?.getBoolean("some.other.extra") }.isEqualTo(true)
//            }
//        }
//    }
//})
