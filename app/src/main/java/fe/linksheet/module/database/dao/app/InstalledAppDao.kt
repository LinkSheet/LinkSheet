package fe.linksheet.module.database.dao.app

import androidx.room.Dao
import fe.linksheet.module.database.dao.base.BaseDao
import fe.linksheet.module.database.entity.app.AppDomainVerificationState
import fe.linksheet.module.database.entity.app.InstalledApp

@Dao
interface InstalledAppDao : BaseDao<InstalledApp> {

}

@Dao
interface AppDomainVerificationStateDao : BaseDao<AppDomainVerificationState> {

}
