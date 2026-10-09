package app.linksheet.test.e2e

import android.content.Intent
import android.net.Uri
import androidx.test.uiautomator.uiAutomator
import app.linksheet.test.e2e.dialog.AnalyticsDialog
import app.linksheet.test.e2e.dialog.RemoteConfigDialog
import app.linksheet.test.e2e.story.ChromeSetupStory
import app.linksheet.test.e2e.story.DefaultBrowserStory
import app.linksheet.test.e2e.story.GoHomeStory
import app.linksheet.test.e2e.story.LaunchAppStory
import app.linksheet.test.e2e.story.LauncherStory
import app.linksheet.test.e2e.usecase.BottomSheetUseCase
import fe.linksheet.testlib.instrument.ui.UiAutomatorTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CoreUseCaseTest : UiAutomatorTest() {
    @JvmField
    @RegisterExtension
    val defaultBrowserExtension = createDefaultBrowserExtension()

    @Test
    fun test(@DefaultBrowserParameter defaultBrowser: DefaultBrowser) = uiAutomator {
        with(GoHomeStory) { awaitHome() }
        if (device.launcherPackageName in LauncherStory.PACKAGE_NAMES) {
            with(LauncherStory) { dismissSwipeNudge() }
        }
        with(LaunchAppStory) { launch(testApp.packageName) }

        watchFor(RemoteConfigDialog(device)) { clickEnable() }
        watchFor(AnalyticsDialog(device)) { clickSave() }
        with(DefaultBrowserStory) { setAsDefaultBrowser(testApp.label) }
        with(GoHomeStory) { awaitHome() }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://linksheet.app"))
        startActivityIntent(intent)

        with(BottomSheetUseCase) {
            expandSheet()
            openJustOnce(defaultBrowser.intentHandlerLabel, defaultBrowser.packageName)
        }

        if (defaultBrowser.applicationLabel == "Chrome") {
            with(ChromeSetupStory) { maybeDismissSetup() }
        }
    }
}
