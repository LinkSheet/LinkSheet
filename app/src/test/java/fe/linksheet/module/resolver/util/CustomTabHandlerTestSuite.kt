package fe.linksheet.module.resolver.util

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.mozilla.components.support.utils.toSafeIntent
import assertk.assertThat
import assertk.assertions.isDataClassEqualTo
import fe.composekit.intent.buildIntent
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.BAKLAVA])
class CustomTabHandlerTest : BaseUnitTest {
    companion object {
        private val customTabIntent = buildIntent(Intent.ACTION_VIEW, "http://developer.chrome.com".toUri()) {
            putExtra("android.support.customtabs.extra.SESSION", null as String?)
            putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true)
            putExtra("androidx.browser.customtabs.extra.TOOLBAR_CORNER_RADIUS_DP", 8)
            putExtra("androidx.browser.customtabs.extra.INITIAL_ACTIVITY_HEIGHT_IN_PIXEL", 600)
            putExtra("androidx.browser.customtabs.extra.ENABLE_BACKGROUND_INTERACTION", 2)
            putExtra("androidx.browser.customtabs.extra.TOOLBAR_CORNER_RADIUS_IN_PIXEL", 21)
            putExtra("androidx.browser.customtabs.extra.INITIAL_ACTIVITY_HEIGHT_PX", 600)
            putExtra("androidx.browser.customtabs.extra.ACTIVITY_HEIGHT_RESIZE_BEHAVIOR", 2)
            putExtra("androidx.browser.customtabs.extra.SHARE_STATE", 0)
            putExtra("com.android.browser.headers", Bundle())
            putExtra("org.chromium.chrome.browser.customtabs.EXTRA_DISABLE_STAR_BUTTON", true)
            putExtra("some.other.extra", true)
        }.toSafeIntent()
        private val nonCustomTabIntent =
            buildIntent(Intent.ACTION_VIEW, "http://developer.chrome.com".toUri()).toSafeIntent()
    }

    @org.junit.Test
    fun `custom tab handling keeps relevant extras, drops non-relevant ones`() {
        assertThat(CustomTabHandler.getInfo(customTabIntent, true)).isDataClassEqualTo(
            CustomTabInfo(
                true,
                listOf("some.other.extra")
            )
        )
        assertThat(
            CustomTabHandler.getInfo2(
                customTabIntent,
                true
            )
        ).isDataClassEqualTo(CustomTabInfo2.Allowed(listOf("some.other.extra")))
    }

    @org.junit.Test
    fun `custom tab handling, don't allow custom tab`() {
        assertThat(CustomTabHandler.getInfo(customTabIntent, false)).isDataClassEqualTo(CustomTabInfo(false, null))
        assertThat(CustomTabHandler.getInfo2(customTabIntent, false)).isDataClassEqualTo(CustomTabInfo2.NotAllowed)
    }

    @org.junit.Test
    fun `non custom tab intent is detected as such`() {
        assertThat(CustomTabHandler.getInfo(nonCustomTabIntent, true)).isDataClassEqualTo(CustomTabInfo(false, null))
        assertThat(CustomTabHandler.getInfo(nonCustomTabIntent, false)).isDataClassEqualTo(CustomTabInfo(false, null))
        assertThat(CustomTabHandler.getInfo2(nonCustomTabIntent, true)).isDataClassEqualTo(CustomTabInfo2.NotApplicable)
        assertThat(
            CustomTabHandler.getInfo2(
                nonCustomTabIntent,
                false
            )
        ).isDataClassEqualTo(CustomTabInfo2.NotApplicable)
    }
}

//val CustomTabHandlerTestSuite by testSuite(testConfig = TestConfig.baseTest()) {
////    for (apiLevel in Build.VERSION_CODES.O..Build.VERSION_CODES.BAKLAVA) {
////    for (apiLevel in listOf(Build.VERSION_CODES.BAKLAVA)) {
//    val apiLevel = Build.VERSION_CODES.UPSIDE_DOWN_CAKE
//    robolectricTestSuite<CustomTabHandlerTestSuiteImpl>(
//        "API level $apiLevel",
//        testConfig = TestConfig.robolectric {
//            sdk = apiLevel
//            qualifiers = "xlarge-port"
//            applicationLifetime = ApplicationLifetime.Test
//        }
//    )
////    }
//}

//internal class CustomTabHandlerTestSuiteImpl : RobolectricTestSuiteContent({
//    val customTabIntent = buildIntent(Intent.ACTION_VIEW, "http://developer.chrome.com".toUri()) {
//        putExtra("android.support.customtabs.extra.SESSION", null as String?)
//        putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true)
//        putExtra("androidx.browser.customtabs.extra.TOOLBAR_CORNER_RADIUS_DP", 8)
//        putExtra("androidx.browser.customtabs.extra.INITIAL_ACTIVITY_HEIGHT_IN_PIXEL", 600)
//        putExtra("androidx.browser.customtabs.extra.ENABLE_BACKGROUND_INTERACTION", 2)
//        putExtra("androidx.browser.customtabs.extra.TOOLBAR_CORNER_RADIUS_IN_PIXEL", 21)
//        putExtra("androidx.browser.customtabs.extra.INITIAL_ACTIVITY_HEIGHT_PX", 600)
//        putExtra("androidx.browser.customtabs.extra.ACTIVITY_HEIGHT_RESIZE_BEHAVIOR", 2)
//        putExtra("androidx.browser.customtabs.extra.SHARE_STATE", 0)
//        putExtra("com.android.browser.headers", Bundle())
//        putExtra("org.chromium.chrome.browser.customtabs.EXTRA_DISABLE_STAR_BUTTON", true)
//        putExtra("some.other.extra", true)
//    }.toSafeIntent()
//    val nonCustomTabIntent = buildIntent(Intent.ACTION_VIEW, "http://developer.chrome.com".toUri()).toSafeIntent()
//
//    test("custom tab handling keeps relevant extras, drops non-relevant ones") {
//        assertThat(CustomTabHandler.getInfo(customTabIntent, true)).isDataClassEqualTo(
//            CustomTabInfo(
//                true,
//                listOf("some.other.extra")
//            )
//        )
//        assertThat(
//            CustomTabHandler.getInfo2(
//                customTabIntent,
//                true
//            )
//        ).isDataClassEqualTo(CustomTabInfo2.Allowed(listOf("some.other.extra")))
//    }
//
//    test("custom tab handling, don't allow custom tab") {
//        assertThat(CustomTabHandler.getInfo(customTabIntent, false)).isDataClassEqualTo(CustomTabInfo(false, null))
//        assertThat(CustomTabHandler.getInfo2(customTabIntent, false)).isDataClassEqualTo(CustomTabInfo2.NotAllowed)
//    }
//
//    test("non custom tab intent is detected as such") {
//        assertThat(CustomTabHandler.getInfo(nonCustomTabIntent, true)).isDataClassEqualTo(CustomTabInfo(false, null))
//        assertThat(CustomTabHandler.getInfo(nonCustomTabIntent, false)).isDataClassEqualTo(CustomTabInfo(false, null))
//        assertThat(CustomTabHandler.getInfo2(nonCustomTabIntent, true)).isDataClassEqualTo(CustomTabInfo2.NotApplicable)
//        assertThat(
//            CustomTabHandler.getInfo2(
//                nonCustomTabIntent,
//                false
//            )
//        ).isDataClassEqualTo(CustomTabInfo2.NotApplicable)
//    }
//})
