package fe.linksheet.module.database.dao.module

import fe.linksheet.module.database.LinkSheetDatabase
import fe.linksheet.module.database.databaseModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val daoModule = module {
    includes(databaseModule)

    singleOf(LinkSheetDatabase::preferredAppDao)
    singleOf(LinkSheetDatabase::disableInAppBrowserInSelectedDao)
    singleOf(LinkSheetDatabase::whitelistedBrowsersDao)
    singleOf(LinkSheetDatabase::whitelistedInAppBrowsersDao)
    singleOf(LinkSheetDatabase::libRedirectDefaultDao)
    singleOf(LinkSheetDatabase::libRedirectServiceStateDao)
    singleOf(LinkSheetDatabase::resolvedRedirectDao)
    singleOf(LinkSheetDatabase::appSelectionHistoryDao)
    singleOf(LinkSheetDatabase::amp2HtmlMappingDao)
}
