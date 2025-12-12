@file:OptIn(ExperimentalTime::class)

package app.linksheet.feature.wiki

import app.linksheet.feature.wiki.database.WikiDatabase
import app.linksheet.feature.wiki.database.repository.WikiCacheRepository
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

val WikiFeatureModule = module {
    single<WikiDatabase> { WikiDatabase.create(context = get(), name = "wiki") }
    factory {
        WikiCacheRepository(dao = get<WikiDatabase>().wikiCacheDao(), clock = get())
    }
}
