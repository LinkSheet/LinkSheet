package fe.linksheet.module.resolver

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import fe.composekit.core.AndroidAppPackage
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.BAKLAVA])
class InAppBrowserHandlerTest : BaseUnitTest {
    companion object {
        private val DISABLED_IN_PACKAGE = "com.twitter.android"
        private val packagesToStateMap = mapOf(
            DISABLED_IN_PACKAGE to true,
            null to false,
            "com.google.android.gm" to false
        )
        private val disabledPackages = packagesToStateMap.filterValues { it }.keys.mapNotNull { it }.toSet()
        private val handler = InAppBrowserHandler { disabledPackages }
    }

    @org.junit.Test
    fun `shouldAllowCustomTab returns false`() = runTest {
        for ((pkg, disabled) in packagesToStateMap) {
            val result = handler.shouldAllowCustomTab(
                pkg?.let { AndroidAppPackage(it) },
                InAppBrowserHandler.InAppBrowserMode.AlwaysDisableInAppBrowser
            )
            assertThat(result).isFalse()
        }
    }

    @org.junit.Test
    fun `shouldAllowCustomTab returns true`() = runTest {
        for ((pkg, disabled) in packagesToStateMap) {
            val result = handler.shouldAllowCustomTab(
                pkg?.let { AndroidAppPackage(it) },
                InAppBrowserHandler.InAppBrowserMode.UseAppSettings
            )
            assertThat(result).isTrue()
        }
    }

    @org.junit.Test
    fun `shouldAllowCustomTab for returns expected`() = runTest {
        for ((pkg, disabled) in packagesToStateMap) {
            val expected = !disabled
            val result = handler.shouldAllowCustomTab(
                pkg?.let { AndroidAppPackage(it) },
                InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps
            )
            assertThat(result).isEqualTo(expected)
        }
    }
}

//val InAppBrowserHandlerTestSuite by testSuite(testConfig = TestConfig.baseTest()) {
////    for (apiLevel in Build.VERSION_CODES.O..Build.VERSION_CODES.BAKLAVA) {
//    for (apiLevel in listOf(Build.VERSION_CODES.BAKLAVA)) {
//        robolectricTestSuite<InAppBrowserHandlerTestSuiteImpl>(
//            "API level $apiLevel",
//            testConfig = TestConfig.robolectric {
//                sdk = apiLevel
//                qualifiers = "xlarge-port"
//                applicationLifetime = ApplicationLifetime.Test
//            }
//        )
//    }
//}
//
//internal class InAppBrowserHandlerTestSuiteImpl : RobolectricTestSuiteContent({
//    val DISABLED_IN_PACKAGE = "com.twitter.android"
//    val packagesToStateMap = mapOf(
//        DISABLED_IN_PACKAGE to true,
//        null to false,
//        "com.google.android.gm" to false
//    )
//    val disabledPackages = packagesToStateMap.filterValues { it }.keys.mapNotNull { it }.toSet()
//    val handler = InAppBrowserHandler { disabledPackages }
//
//    testSuite("AlwaysDisableInAppBrowser") {
//        for ((pkg, disabled) in packagesToStateMap) {
//            test("shouldAllowCustomTab for '$pkg' returns false") {
//                val result = handler.shouldAllowCustomTab(
//                    pkg?.let { AndroidAppPackage(it) },
//                    InAppBrowserHandler.InAppBrowserMode.AlwaysDisableInAppBrowser
//                )
//                assertThat(result).isFalse()
//            }
//        }
//    }
//    testSuite("UseAppSettings") {
//        for ((pkg, disabled) in packagesToStateMap) {
//            test("shouldAllowCustomTab for '$pkg' returns true") {
//                val result = handler.shouldAllowCustomTab(
//                    pkg?.let { AndroidAppPackage(it) },
//                    InAppBrowserHandler.InAppBrowserMode.UseAppSettings
//                )
//                assertThat(result).isTrue()
//            }
//        }
//    }
//    testSuite("DisableInSelectedApps") {
//        for ((pkg, disabled) in packagesToStateMap) {
//            val expected = !disabled
//            test("shouldAllowCustomTab for '$pkg' returns $expected") {
//                val result = handler.shouldAllowCustomTab(
//                    pkg?.let { AndroidAppPackage(it) },
//                    InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps
//                )
//                assertThat(result).isEqualTo(expected)
//            }
//        }
//    }
//})
