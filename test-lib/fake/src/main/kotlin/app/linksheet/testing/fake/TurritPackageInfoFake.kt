package app.linksheet.testing.fake

import app.linksheet.testing.util.addHosts
import app.linksheet.testing.util.buildIntentFilter
import app.linksheet.testing.util.buildPackageInfoTestFakeLazy

private val intentFilter = buildIntentFilter {
    addAction("android.intent.action.VIEW")
    addCategory("android.intent.category.DEFAULT")
    addCategory("android.intent.category.BROWSABLE")
    addDataScheme("http")
    addDataScheme("https")
    addHosts("telegram.me", "telegram.dog", "t.me")
}

object TurritPackageInfoFake {
    const val LaunchActivity = "org.telegram.ui.LaunchActivity"
    val PackageInfo by buildPackageInfoTestFakeLazy("org.telegram.group", "Turrit") {
        activity("com.turrit.slidemenu.FragmentContainerActivity", false) {
            addFilter(intentFilter)
        }

        activity(LaunchActivity) {
            addFilter(intentFilter)
        }
    }
}
