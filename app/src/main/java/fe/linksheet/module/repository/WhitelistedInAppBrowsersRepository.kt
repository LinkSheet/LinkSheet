package fe.linksheet.module.repository

import fe.linksheet.module.database.dao.WhitelistedInAppBrowsersDao
import fe.linksheet.module.database.entity.WhitelistedInAppBrowser
import fe.linksheet.module.repository.base.WhitelistedBrowsersRepository


class WhitelistedInAppBrowsersRepository(
    val dao: WhitelistedInAppBrowsersDao
) : WhitelistedBrowsersRepository<WhitelistedInAppBrowser, WhitelistedInAppBrowser.Creator, WhitelistedInAppBrowsersDao>(
    dao
)