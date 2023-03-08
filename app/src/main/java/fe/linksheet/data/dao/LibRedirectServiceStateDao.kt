package fe.linksheet.data.dao

import androidx.room.*
import fe.linksheet.data.entity.LibRedirectServiceState
import fe.linksheet.data.entity.ResolvedRedirect


@Dao
interface LibRedirectServiceStateDao {
    @Query("SELECT * FROM lib_redirect_service_state WHERE serviceKey = :serviceKey")
    fun getLibRedirectServiceState(serviceKey: String): LibRedirectServiceState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(libRedirectServiceState: LibRedirectServiceState)
}