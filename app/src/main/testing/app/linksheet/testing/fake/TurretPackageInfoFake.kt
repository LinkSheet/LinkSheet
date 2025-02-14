package app.linksheet.testing.fake

import app.linksheet.testing.util.addHosts
import app.linksheet.testing.util.buildIntentFilter
import app.linksheet.testing.util.buildPackageInfoTestFake

private val intentFilter = buildIntentFilter {
    addAction("android.intent.action.VIEW")
    addCategory("android.intent.category.DEFAULT")
    addCategory("android.intent.category.BROWSABLE")
    addDataScheme("http")
    addDataScheme("https")
    addHosts("telegram.me", "telegram.dog", "t.me")
}

val TurretPackageInfoFake = buildPackageInfoTestFake("org.telegram.group", "Turret") {
    activity("com.turrit.slidemenu.FragmentContainerActivity", false) {
        addFilter(intentFilter)
    }

    activity("org.telegram.ui.LaunchActivity") {
        addFilter(intentFilter)
    }
}
