package fe.linksheet.module.resolver

import android.os.Build
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import de.infix.testBalloon.framework.core.TestConfig
import de.infix.testBalloon.framework.core.testSuite
import de.infix.testBalloon.integration.robolectric.ApplicationLifetime
import de.infix.testBalloon.integration.robolectric.RobolectricTestSuiteContent
import de.infix.testBalloon.integration.robolectric.robolectric
import de.infix.testBalloon.integration.robolectric.robolectricTestSuite
import fe.composekit.core.AndroidAppPackage

val InAppBrowserHandlerTestSuite by testSuite {
//    for (apiLevel in Build.VERSION_CODES.O..Build.VERSION_CODES.BAKLAVA) {
    for (apiLevel in listOf(Build.VERSION_CODES.BAKLAVA)) {
        robolectricTestSuite<InAppBrowserHandlerTestSuiteImpl>(
            "API level $apiLevel",
            testConfig = TestConfig.robolectric {
                sdk = apiLevel
                qualifiers = "xlarge-port"
                applicationLifetime = ApplicationLifetime.RobolectricTestSuite
            }
        )
    }
}

internal class InAppBrowserHandlerTestSuiteImpl : RobolectricTestSuiteContent({
    val DISABLED_IN_PACKAGE = "com.twitter.android"
    val packagesToStateMap = mapOf(
        DISABLED_IN_PACKAGE to true,
        null to false,
        "com.google.android.gm" to false
    )
    val disabledPackages = packagesToStateMap.filterValues { it }.keys.mapNotNull { it }.toSet()
    val handler = InAppBrowserHandler { disabledPackages }

    testSuite("AlwaysDisableInAppBrowser") {
        for ((pkg, disabled) in packagesToStateMap) {
            test("shouldAllowCustomTab for '$pkg' returns false") {
                val result = handler.shouldAllowCustomTab(
                    pkg?.let { AndroidAppPackage(it) },
                    InAppBrowserHandler.InAppBrowserMode.AlwaysDisableInAppBrowser
                )
                assertThat(result).isFalse()
            }
        }
    }
    testSuite("UseAppSettings") {
        for ((pkg, disabled) in packagesToStateMap) {
            test("shouldAllowCustomTab for '$pkg' returns true") {
                val result = handler.shouldAllowCustomTab(
                    pkg?.let { AndroidAppPackage(it) },
                    InAppBrowserHandler.InAppBrowserMode.UseAppSettings
                )
                assertThat(result).isTrue()
            }
        }
    }
    testSuite("DisableInSelectedApps") {
        for ((pkg, disabled) in packagesToStateMap) {
            val expected = !disabled
            test("shouldAllowCustomTab for '$pkg' returns $expected") {
                val result = handler.shouldAllowCustomTab(
                    pkg?.let { AndroidAppPackage(it) },
                    InAppBrowserHandler.InAppBrowserMode.DisableInSelectedApps
                )
                assertThat(result).isEqualTo(expected)
            }
        }
    }
})
