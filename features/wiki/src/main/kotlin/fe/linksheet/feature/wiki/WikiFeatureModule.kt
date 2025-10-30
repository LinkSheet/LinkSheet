@file:OptIn(ExperimentalTime::class)

package fe.linksheet.feature.wiki

import fe.linksheet.feature.wiki.database.WikiDatabase
import fe.linksheet.feature.wiki.database.repository.WikiCacheRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

val WikiFeatureModule = module {
    single<WikiDatabase> { WikiDatabase.create(context = get(), name = "wiki") }
    singleOf(WikiDatabase::wikiCacheDao)
    singleOf(::WikiCacheRepository)
}
