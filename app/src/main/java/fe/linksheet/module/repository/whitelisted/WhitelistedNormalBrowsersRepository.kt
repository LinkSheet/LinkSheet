package fe.linksheet.module.repository.whitelisted

import fe.linksheet.module.database.dao.whitelisted.WhitelistedNormalBrowsersDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedNormalBrowser


class WhitelistedNormalBrowsersRepository(
    val dao: WhitelistedNormalBrowsersDao
) : WhitelistedBrowsersRepository<WhitelistedNormalBrowser, WhitelistedNormalBrowser.Creator, WhitelistedNormalBrowsersDao>(
    dao
)