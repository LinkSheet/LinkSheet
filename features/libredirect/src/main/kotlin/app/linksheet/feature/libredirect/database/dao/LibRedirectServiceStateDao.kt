package app.linksheet.feature.libredirect.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectServiceState
import kotlinx.coroutines.flow.Flow


@Dao
interface LibRedirectServiceStateDao : BaseDao<LibRedirectServiceState> {
    @Query("SELECT * FROM lib_redirect_service_state WHERE serviceKey = :serviceKey")
    fun getServiceState(serviceKey: String): Flow<LibRedirectServiceState?>
}
