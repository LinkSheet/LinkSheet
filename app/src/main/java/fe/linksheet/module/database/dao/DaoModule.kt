package fe.linksheet.module.database.dao

import fe.linksheet.module.database.LinkSheetDatabase
import fe.linksheet.module.database.databaseModule
import org.koin.dsl.module

val daoModule = module {
    includes(databaseModule)

    single { get<LinkSheetDatabase>().preferredAppDao() }
    single { get<LinkSheetDatabase>().disableInAppBrowserInSelectedDao() }
    single { get<LinkSheetDatabase>().whitelistedBrowsersDao() }
    single { get<LinkSheetDatabase>().whitelistedInAppBrowsersDao() }
    single { get<LinkSheetDatabase>().libRedirectDefaultDao() }
    single { get<LinkSheetDatabase>().libRedirectServiceStateDao() }
    single { get<LinkSheetDatabase>().resolvedRedirectDao() }
    single { get<LinkSheetDatabase>().appSelectionHistoryDao() }
}