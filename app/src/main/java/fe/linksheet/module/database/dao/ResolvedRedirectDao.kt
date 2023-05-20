package fe.linksheet.module.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fe.linksheet.module.database.dao.base.BaseDao
import fe.linksheet.module.database.entity.ResolvedRedirect
import kotlinx.coroutines.flow.Flow

@Dao
interface ResolvedRedirectDao : BaseDao<ResolvedRedirect> {
    @Query("SELECT * FROM resolved_redirect WHERE shortUrl = :shortUrl")
    fun getForShortUrl(shortUrl: String): Flow<ResolvedRedirect?>
}