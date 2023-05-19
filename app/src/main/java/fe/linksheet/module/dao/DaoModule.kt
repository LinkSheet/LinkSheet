package fe.linksheet.module.dao

import com.tasomaniac.openwith.data.LinkSheetDatabase
import fe.linksheet.module.database.databaseModule
import org.koin.dsl.module

val daoModule = module {
    includes(databaseModule)

    single { get<LinkSheetDatabase>().preferredAppDao() }
    single { get<LinkSheetDatabase>().disableInAppBrowserInSelectedDao() }
    single { get<LinkSheetDatabase>().whitelistedBrowsersDao() }
    single { get<LinkSheetDatabase>().libRedirectDefaultDao() }
    single { get<LinkSheetDatabase>().libRedirectServiceStateDao() }
    single { get<LinkSheetDatabase>().resolvedRedirectDao() }
    single { get<LinkSheetDatabase>().appSelectionHistoryDao() }
}