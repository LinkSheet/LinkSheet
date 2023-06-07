package fe.linksheet.module.repository.module

import fe.linksheet.module.database.dao.module.daoModule
import fe.linksheet.module.database.databaseModule
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.resolver.ResolvedRedirectRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.repository.resolver.Amp2HtmlRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    includes(databaseModule, daoModule)

    singleOf(::PreferredAppRepository)
    singleOf(::DisableInAppBrowserInSelectedRepository)
    singleOf(::WhitelistedNormalBrowsersRepository)
    singleOf(::WhitelistedInAppBrowsersRepository)
    singleOf(::AppSelectionHistoryRepository)
    singleOf(::LibRedirectDefaultRepository)
    singleOf(::LibRedirectStateRepository)
    singleOf(::ResolvedRedirectRepository)
    singleOf(::Amp2HtmlRepository)
}

