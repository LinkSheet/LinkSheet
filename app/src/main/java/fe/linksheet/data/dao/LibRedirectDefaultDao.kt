package fe.linksheet.data.dao

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.data.dao.base.BaseDao
import fe.linksheet.data.entity.LibRedirectDefault

@Dao
interface LibRedirectDefaultDao : BaseDao<LibRedirectDefault> {
    @Query("SELECT * FROM lib_redirect_default WHERE serviceKey = :serviceKey")
    fun getByServiceKey(serviceKey: String): LibRedirectDefault?
}