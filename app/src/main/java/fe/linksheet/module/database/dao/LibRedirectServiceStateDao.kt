package fe.linksheet.module.database.dao

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.common.dao.base.BaseDao
import fe.linksheet.module.database.entity.LibRedirectServiceState
import kotlinx.coroutines.flow.Flow


@Dao
interface LibRedirectServiceStateDao : BaseDao<LibRedirectServiceState> {
    @Query("SELECT * FROM lib_redirect_service_state WHERE serviceKey = :serviceKey")
    fun getServiceState(serviceKey: String): Flow<LibRedirectServiceState?>
}
