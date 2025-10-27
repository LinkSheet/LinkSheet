@file:OptIn(ExperimentalTime::class)

package fe.linksheet.module.repository.module

import app.linksheet.feature.engine.database.repository.CacheRepository
import fe.linksheet.feature.wiki.WikiCacheRepository
import fe.linksheet.module.clock.ClockModule
import fe.linksheet.module.database.DatabaseModule
import fe.linksheet.module.database.dao.module.DaoModule
import fe.linksheet.module.repository.*
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

val RepositoryModule = module {
    includes(ClockModule, DatabaseModule, DaoModule)

    singleOf(::PreferredAppRepository)
    singleOf(::DisableInAppBrowserInSelectedRepository)
    singleOf(::WhitelistedNormalBrowsersRepository)
    singleOf(::WhitelistedInAppBrowsersRepository)
    singleOf(::AppSelectionHistoryRepository)

    singleOf(::ResolvedRedirectRepository)
    singleOf(::Amp2HtmlRepository)
    singleOf(::WikiCacheRepository)

}
