package fe.linksheet.module.repository

import fe.linksheet.module.database.dao.WhitelistedNormalBrowsersDao
import fe.linksheet.module.database.entity.WhitelistedNormalBrowser
import fe.linksheet.module.repository.base.WhitelistedBrowsersRepository


class WhitelistedNormalBrowsersRepository(
    val dao: WhitelistedNormalBrowsersDao
) : WhitelistedBrowsersRepository<WhitelistedNormalBrowser, WhitelistedNormalBrowser.Creator, WhitelistedNormalBrowsersDao>(
    dao
)