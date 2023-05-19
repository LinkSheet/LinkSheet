package fe.linksheet.module.repository

import fe.linksheet.module.dao.daoModule
import fe.linksheet.module.database.databaseModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    includes(databaseModule, daoModule)

    singleOf(::PreferredAppRepository)
    singleOf(::DisableInAppBrowserInSelectedRepository)
    singleOf(::PreferredBrowserRepository)
}

