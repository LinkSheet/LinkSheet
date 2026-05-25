package app.linksheet.test.e2e.story

import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiAutomatorTestScope
import androidx.test.uiautomator.simpleViewResourceName

object LauncherStory {
    val PACKAGE_NAMES = setOf("com.google.android.apps.nexuslauncher", "com.android.launcher3")

    fun UiAutomatorTestScope.dismissSwipeNudge() {
        // If the user has never swiped up before [to see the app list], Pixel launcher/Launcher3 tries to nudge the user
        // into swiping up by having the dock "bounce" up slightly to indicate that it's swipeable; Unfortunately,
        // this seems to mess with the bottom sheet interaction when few apps are present
        // => Try to dismiss nudge by swiping up once
        val dragLayer = onElement {
            packageName in PACKAGE_NAMES && simpleViewResourceName() == "drag_layer"
        }
        dragLayer.swipe(Direction.UP, 0.1f)
        pressHome()
    }
}
