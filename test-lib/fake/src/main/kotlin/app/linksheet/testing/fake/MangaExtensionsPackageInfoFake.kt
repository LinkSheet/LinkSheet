package app.linksheet.testing.fake

import app.linksheet.testing.util.buildPackageInfoTestFake

val MangaExtensionsPackageInfoFake = buildPackageInfoTestFake("eu.kanade.tachiyomi.extension", "AnyWebpage Scraper") {
    activity(".all.anyweb.AnyWebUrlActivity") {
        addFilter {
            addAction("android.intent.action.VIEW")
            addCategory("android.intent.category.DEFAULT")
            addCategory("android.intent.category.BROWSABLE")
            addDataScheme("http")
            addDataScheme("https")
        }
    }
    activity(".all.anyweb.AnyWebIndexUrlActivity") {
        addFilter {
            addAction("android.intent.action.VIEW")
            addCategory("android.intent.category.DEFAULT")
            addCategory("android.intent.category.BROWSABLE")
            addDataScheme("http")
            addDataScheme("https")
        }
    }
}
