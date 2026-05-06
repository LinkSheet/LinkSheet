package app.linksheet.feature.libredirect.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectUserInstance
import kotlinx.coroutines.flow.Flow

@Dao
interface LibRedirectUserInstanceDao : BaseDao<LibRedirectUserInstance> {
    @Query("SELECT * FROM lib_redirect_user_instance WHERE serviceKey = :serviceKey AND frontendKey = :frontend")
    fun getByServiceAndFrontendOrNull(serviceKey: String, frontend: String): Flow<List<LibRedirectUserInstance>>
}
