package fe.linksheet.data.dao

import androidx.room.*
import fe.linksheet.data.dao.base.BaseDao
import fe.linksheet.data.entity.LibRedirectServiceState


@Dao
interface LibRedirectServiceStateDao : BaseDao<LibRedirectServiceState> {
    @Query("SELECT * FROM lib_redirect_service_state WHERE serviceKey = :serviceKey")
    fun getServiceState(serviceKey: String): LibRedirectServiceState?
}