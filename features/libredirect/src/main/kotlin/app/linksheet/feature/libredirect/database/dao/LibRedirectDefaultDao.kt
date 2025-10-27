package app.linksheet.feature.libredirect.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import kotlinx.coroutines.flow.Flow

@Dao
interface LibRedirectDefaultDao : BaseDao<LibRedirectDefault> {
    @Query("SELECT * FROM lib_redirect_default WHERE serviceKey = :serviceKey")
    fun getByServiceKey(serviceKey: String): Flow<LibRedirectDefault?>
}
