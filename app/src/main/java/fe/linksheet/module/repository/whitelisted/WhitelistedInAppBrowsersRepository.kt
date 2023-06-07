package fe.linksheet.module.repository.whitelisted

import fe.linksheet.module.database.dao.whitelisted.WhitelistedInAppBrowsersDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedInAppBrowser


class WhitelistedInAppBrowsersRepository(
    val dao: WhitelistedInAppBrowsersDao
) : WhitelistedBrowsersRepository<WhitelistedInAppBrowser, WhitelistedInAppBrowser.Creator, WhitelistedInAppBrowsersDao>(
    dao
)