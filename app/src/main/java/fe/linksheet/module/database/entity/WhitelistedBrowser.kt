package fe.linksheet.module.database.entity

import fe.linksheet.module.database.dao.base.PackageEntity

abstract class WhitelistedBrowser<T>(packageName: String) :
    PackageEntity<T>(packageName)