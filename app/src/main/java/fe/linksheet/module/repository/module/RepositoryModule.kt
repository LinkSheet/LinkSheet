@file:OptIn(ExperimentalTime::class)

package fe.linksheet.module.repository.module

import fe.linksheet.module.clock.ClockModule
import fe.linksheet.module.database.DatabaseModule
import fe.linksheet.module.database.LinkSheetDatabase
import fe.linksheet.module.database.dao.module.DaoModule
import fe.linksheet.module.repository.*
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

val RepositoryModule = module {
    includes(ClockModule, DatabaseModule, DaoModule)

    factory { PreferredAppRepository(dao = get<LinkSheetDatabase>().preferredAppDao()) }
    factory { DisableInAppBrowserInSelectedRepository(dao = get<LinkSheetDatabase>().disableInAppBrowserInSelectedDao()) }
    factory { WhitelistedNormalBrowsersRepository(dao = get<LinkSheetDatabase>().whitelistedBrowsersDao()) }
    factory { WhitelistedInAppBrowsersRepository(dao = get<LinkSheetDatabase>().whitelistedInAppBrowsersDao()) }
    factory { AppSelectionHistoryRepository(dao = get<LinkSheetDatabase>().appSelectionHistoryDao()) }
    factory { ResolvedRedirectRepository(dao = get<LinkSheetDatabase>().resolvedRedirectDao()) }
    factory { Amp2HtmlRepository(dao = get<LinkSheetDatabase>().amp2HtmlMappingDao()) }
}
